package org.todaybook.embedding.infrastructure.batch.reader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.batch.model.Book;
import org.todaybook.embedding.infrastructure.batch.model.BookCursor;
import org.todaybook.embedding.infrastructure.batch.model.BookMapper;

/**
 * Keyset 기반 페이징을 사용하는 Spring Batch {@link ItemReader}.
 *
 * <p>이 Reader는 {@code updated_at ASC, id ASC} 정렬을 기준으로 데이터를 조회하며, 동일한 {@code updated_at} 값을 가지는
 * 레코드에 대해 {@code id}를 tie-breaker(보조 기준)으로 사용한다.
 *
 * <p>마지막으로 처리한 레코드의 커서 정보({@code updated_at, id})는 {@link ExecutionContext}에 저장되며, Step 재시작 시 해당
 * 위치부터 이어서 처리된다.
 *
 * <p>Offset 기반 Paging의 성능 문제를 피하고, 대용량 테이블에서도 안정적인 배치 처리를 수행하기 위해 Keyset Pagination 방식을 사용한다.
 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class KeysetItemReader implements ItemReader<Book>, StepExecutionListener {

  private final JdbcTemplate jdbcTemplate;

  private BookCursor cursor;
  private Iterator<Book> iterator = Collections.emptyIterator();

  @Value("#{jobParameters['chunkSize'] ?: 50}")
  private int pageSize;

  private static final String CURSOR_ID = "cursor.id";
  private static final String CURSOR_UPDATED_AT = "cursor.updated_at";

  /**
   * 다음 {@link Book} 엔티티를 반환한다.
   *
   * <p>현재 페이지의 데이터가 소진되면 DB에서 다음 페이지를 조회(fetch)한다. 더 이상 조회할 데이터가 없을 경우 {@code null}을 반환하여 Step을
   * 종료한다.
   *
   * @return 다음 처리할 {@link Book}, 없으면 {@code null}
   */
  @Override
  public Book read() {
    if (!iterator.hasNext()) {
      fetch();
    }

    Book book = iterator.hasNext() ? iterator.next() : null;

    if (book != null) {
      log.debug(
          "[TODAY-BOOK] KeysetItemReader - read (bookId={}, updatedAt={})",
          book.id(),
          book.updatedAt());
    }

    return book;
  }

  /**
   * 현재 커서를 기준으로 다음 페이지의 데이터를 조회한다.
   *
   * <p>조회 조건:
   *
   * <ul>
   *   <li>{@code updated_at > lastUpdatedAt}
   *   <li>{@code updated_at == lastUpdatedAt AND id > lastId}
   * </ul>
   *
   * 조회 결과가 존재할 경우, 마지막 레코드를 기준으로 커서를 갱신한다.
   */
  private void fetch() {
    if (cursor == null) {
      log.error("[TODAY-BOOK] Cursor가 초기화되지 않았습니다. 작업을 종료합니다.");
      throw new IllegalStateException("Batch cursor is missing. Job execution must be terminated.");
    }

    log.debug(
        "[TODAY-BOOK] KeysetItemReader - fetch (bookId={}, updatedAt={}, pageSize={})",
        cursor.bookId(),
        cursor.updatedAt(),
        pageSize);

    List<Book> result = new ArrayList<>();
    if (cursor.updatedAt() == null) {
      result.addAll(
          jdbcTemplate.query(
              """
                  SELECT id, isbn, title, categories, description, author, publisher, published_at, created_at, updated_at
                  FROM book.p_books
                  ORDER BY updated_at ASC, id ASC
                  LIMIT ?
                  """,
              new BookMapper(),
              pageSize));
    } else {
      result.addAll(
          jdbcTemplate.query(
              """
                SELECT id, isbn, title, categories, description, author, publisher, published_at, created_at, updated_at
                FROM book.p_books
                WHERE
                  (
                    updated_at > ?
                    OR (updated_at = ? AND id > ?)
                  )
                ORDER BY updated_at ASC, id ASC
                LIMIT ?
                """,
              new BookMapper(),
              cursor.updatedAt(),
              cursor.updatedAt(),
              cursor.bookId(),
              pageSize));
    }

    if (result.isEmpty()) {
      iterator = Collections.emptyIterator();
      return;
    }

    BookCursor prev = cursor;

    Book book = result.getLast();
    cursor = BookCursor.of(book.id(), book.updatedAt());
    iterator = result.iterator();

    log.info(
        "[TODAY-BOOK] KeysetItemReader 커서 이동 ({} -> {})", formatCursor(prev), formatCursor(cursor));
  }

  /** Step 시작 시 ExecutionContext로부터 커서를 복원한다. */
  @Override
  public void beforeStep(StepExecution execution) {
    ExecutionContext context = execution.getExecutionContext();

    if (context.containsKey(CURSOR_ID) && context.containsKey(CURSOR_UPDATED_AT)) {
      cursor =
          BookCursor.of(
              UUID.fromString(context.getString(CURSOR_ID)),
              (LocalDateTime) context.get(CURSOR_UPDATED_AT));
    } else {
      cursor = BookCursor.initial();
    }

    log.info("[TODAY-BOOK] KeysetItemReader 실행 - ({})", formatCursor(cursor));
  }

  /** Step 종료 시 현재 커서를 ExecutionContext에 저장하여 재시작 시 이어서 처리할 수 있도록 한다. */
  @Override
  public ExitStatus afterStep(StepExecution execution) {
    if (cursor != null) {
      ExecutionContext context = execution.getExecutionContext();
      context.put(CURSOR_ID, cursor.bookId().toString());
      context.put(CURSOR_UPDATED_AT, cursor.updatedAt());

      log.info("[TODAY-BOOK] 임베딩 step이 종료되었습니다. cursor를 저장합니다. ({})", formatCursor(cursor));
    } else {
      log.warn("[TODAY-BOOK] 임베딩 step이 종료되었습니다. cursor가 존재하지 않습니다.");
    }
    return ExitStatus.COMPLETED;
  }

  private String formatCursor(BookCursor cursor) {
    if (cursor == null) return "null";
    return "cursor(bookId=" + cursor.bookId() + ", updatedAt=" + cursor.updatedAt() + ")";
  }
}

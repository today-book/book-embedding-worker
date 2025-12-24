package org.todaybook.embedding.domain.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.BookKeyset;
import org.todaybook.embedding.domain.BookMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

  private final JdbcTemplate jdbcTemplate;

  @Override
  @Transactional(readOnly = true)
  public List<Book> findByCursor(BookKeyset keyset, int size) {
    List<Book> result = new ArrayList<>();

    if (keyset.updatedAt() == null) {
      result.addAll(
          jdbcTemplate.query(
              """
                  SELECT id, isbn, title, categories, description, author, publisher, published_at, created_at, updated_at
                  FROM book.p_books
                  ORDER BY updated_at ASC, id ASC
                  LIMIT ?
                  """,
              new BookMapper(),
              size));
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
              keyset.updatedAt(),
              keyset.updatedAt(),
              keyset.bookId(),
              size));
    }

    log.debug(
        "[TODAY-BOOK] 도서 데이터 조회 (bookId={}, updatedAt={}, size={})",
        keyset.bookId(),
        keyset.updatedAt(),
        size);

    return result;
  }
}

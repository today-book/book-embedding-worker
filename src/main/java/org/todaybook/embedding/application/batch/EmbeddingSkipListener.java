package org.todaybook.embedding.application.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.VectorBook;

@Slf4j
@Component
public class EmbeddingSkipListener implements SkipListener<Book, VectorBook> {

  @Override
  public void onSkipInRead(Throwable throwable) {
    log.warn("[TODAY-BOOK] 스킵된 문서 (read 단계), reason={}", throwable.getMessage());
  }

  @Override
  public void onSkipInProcess(Book book, Throwable throwable) {
    log.warn("[TODAY-BOOK] 스킵된 문서 (process 단계) bookId={}, reason={}", book.id(), throwable.getMessage());
  }

  @Override
  public void onSkipInWrite(VectorBook vectorBook, Throwable throwable) {
    log.warn("[TODAY-BOOK] 스킵된 문서 (write 단계) vectorBookId={}, reason={}", vectorBook.id(), throwable.getMessage());
  }
}

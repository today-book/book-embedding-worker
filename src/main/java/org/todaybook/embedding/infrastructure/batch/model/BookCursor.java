package org.todaybook.embedding.infrastructure.batch.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookCursor(UUID bookId, LocalDateTime updatedAt) {
  public static BookCursor of(UUID bookId, LocalDateTime updatedAt) {
    return new BookCursor(bookId, updatedAt);
  }

  public static BookCursor initial() {
    return new BookCursor(null, null);
  }
}

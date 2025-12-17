package org.todaybook.embedding.infrastructure.batch.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.batch.item.ExecutionContext;

public record BookCursor(UUID bookId, LocalDateTime updatedAt) {
  public static BookCursor of(UUID bookId, LocalDateTime updatedAt) {
    return new BookCursor(bookId, updatedAt);
  }

  public static BookCursor from(ExecutionContext context) {
    LocalDateTime updatedAt =
        context.containsKey("updatedAt")
            ? (LocalDateTime) context.get("updatedAt")
            : LocalDateTime.of(1970, 1, 1, 0, 0);

    UUID bookId =
        context.containsKey("bookId") ? UUID.fromString(context.getString("bookId")) : null;

    return new BookCursor(bookId, updatedAt);
  }
}

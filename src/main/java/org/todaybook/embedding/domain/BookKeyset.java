package org.todaybook.embedding.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.batch.item.ExecutionContext;

public record BookKeyset(UUID bookId, LocalDateTime updatedAt) {

  public static final String KEY_BOOK_ID = "keyset.bookId";
  public static final String KEY_UPDATED_AT = "keyset.updatedAt";

  public static BookKeyset of(UUID bookId, LocalDateTime updatedAt) {
    if (bookId == null || updatedAt == null) {
      throw new IllegalArgumentException("bookId and updatedAt must not be null");
    }
    return new BookKeyset(bookId, updatedAt);
  }

  public static BookKeyset initial() {
    return new BookKeyset(
        UUID.fromString("00000000-0000-0000-0000-000000000000"),
        LocalDateTime.of(1970, 1, 1, 0, 0, 0)
    );
  }

  public static boolean exists(ExecutionContext context) {
    return context.containsKey(KEY_BOOK_ID) && context.containsKey(KEY_UPDATED_AT);
  }

  public static BookKeyset from(ExecutionContext context) {
    return BookKeyset.of(
        UUID.fromString(context.getString(KEY_BOOK_ID)),
        (LocalDateTime) context.get(KEY_UPDATED_AT)
    );
  }

  public void put(ExecutionContext context) {
    context.put(KEY_BOOK_ID, bookId.toString());
    context.put(KEY_UPDATED_AT, updatedAt);
  }
}

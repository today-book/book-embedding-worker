package org.todaybook.embedding.domain;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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

  public static boolean exists(Map<String, Object> context) {
    return context.containsKey(KEY_BOOK_ID) && context.containsKey(KEY_UPDATED_AT);
  }

  public static BookKeyset from(Map<String, Object> context) {
    return BookKeyset.of(
        UUID.fromString(context.get(KEY_BOOK_ID).toString()),
        (LocalDateTime) context.get(KEY_UPDATED_AT)
    );
  }

  public void put(Map<String, Object> context) {
    context.put(KEY_BOOK_ID, bookId.toString());
    context.put(KEY_UPDATED_AT, LocalDateTime.now());
  }
}

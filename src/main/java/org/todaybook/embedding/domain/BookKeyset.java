package org.todaybook.embedding.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.batch.item.ExecutionContext;

public record BookKeyset(UUID bookId, LocalDateTime updatedAt) {

  private static final String KEY_BOOK_ID = "keyset.bookId";
  private static final String KEY_UPDATED_AT = "keyset.updatedAt";

  private static final UUID INITIAL_BOOK_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000000");
  private static final LocalDateTime INITIAL_UPDATED_AT = LocalDateTime.of(1970, 1, 1, 0, 0);

  public static BookKeyset of(UUID bookId, LocalDateTime updatedAt) {
    if (bookId == null || updatedAt == null) {
      throw new IllegalArgumentException("bookId and updatedAt must not be null");
    }
    return new BookKeyset(bookId, updatedAt);
  }

  public static BookKeyset initial() {
    return new BookKeyset(INITIAL_BOOK_ID, INITIAL_UPDATED_AT);
  }

  public boolean isInitial() {
    return INITIAL_BOOK_ID.equals(bookId) && INITIAL_UPDATED_AT.equals(updatedAt);
  }

  public static BookKeyset from(ExecutionContext context) {
    if (context == null) {
      return initial();
    }

    Object bookId = context.get(KEY_BOOK_ID);
    Object updatedAt = context.get(KEY_UPDATED_AT);

    if (!(bookId instanceof String) || !(updatedAt instanceof LocalDateTime)) {
      return initial();
    }

    return BookKeyset.of(UUID.fromString((String) bookId), (LocalDateTime) updatedAt);
  }

  public void put(ExecutionContext context) {
    if (isInitial()) return;

    context.put(KEY_BOOK_ID, bookId.toString());
    context.put(KEY_UPDATED_AT, updatedAt);
  }
}

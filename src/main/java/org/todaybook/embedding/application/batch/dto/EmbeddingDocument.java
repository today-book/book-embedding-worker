package org.todaybook.embedding.application.batch.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.todaybook.embedding.domain.Book;

public record EmbeddingDocument(
    UUID id, String content, Map<String, Object> metadata, float[] embedding) {
  public static EmbeddingDocument of(
      UUID id, String content, Map<String, Object> metadata, float[] vector) {
    return new EmbeddingDocument(id, content, metadata, vector);
  }

  public static EmbeddingDocument from(Document document, float[] vector) {
    return new EmbeddingDocument(
        UUID.fromString(document.getId()),
        document.getFormattedContent(),
        document.getMetadata(),
        vector);
  }

  public static String buildContent(Book book) {
    StringBuilder builder = new StringBuilder();
    builder.append("title: ").append(book.title()).append("\n");
    builder.append("author: ").append(book.author()).append("\n");
    builder.append("description: ").append(book.description()).append("\n");

    String categories =
        book.categories() != null ? java.lang.String.join(",", book.categories()) : "";
    builder.append("categories: ").append(categories).append("\n");

    return builder.toString();
  }

  public static Map<String, Object> buildMetadata(Book book) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("bookId", book.id());
    metadata.put("title", book.title());
    metadata.put("author", book.author());
    metadata.put("categories", book.categories() != null ? book.categories() : List.of());

    return metadata;
  }
}

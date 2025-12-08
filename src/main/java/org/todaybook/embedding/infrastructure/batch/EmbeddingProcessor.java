package org.todaybook.embedding.infrastructure.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.VectorBook;

@Component
@RequiredArgsConstructor
public class EmbeddingProcessor implements ItemProcessor<Book, VectorBook> {

  @Override
  public VectorBook process(Book item) {
    return new VectorBook(
        item.id(),
        getContent(item),
        getMetadata(item)
    );
  }

  private static String getContent(Book book) {
    StringBuilder builder = new StringBuilder();
    builder.append("title: ").append(book.title()).append("\n");
    builder.append("author: ").append(book.author()).append("\n");
    builder.append("description: ").append(book.description()).append("\n");

    String categories = book.categories() != null ? String.join(",", book.categories()) : "";
    builder.append("categories: ").append(categories).append("\n");

    return builder.toString();
  }

  private static Map<String, Object> getMetadata(Book book) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("bookId", book.id());
    metadata.put("title", book.title());
    metadata.put("author", book.author());
    metadata.put("categories", book.categories() != null ? book.categories() : List.of());

    return metadata;
  }
}

package org.todaybook.embedding.infrastructure.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.vector.service.VectorService;
import org.todaybook.embedding.domain.VectorBook;

@Component
@RequiredArgsConstructor
public class EmbeddingWriter implements ItemWriter<VectorBook> {

  private final VectorService vectorService;

  @Override
  public void write(Chunk<? extends VectorBook> items) {
    List<String> ids = items.getItems().stream()
        .map(item -> item.id().toString())
        .toList();

    List<String> existing = vectorService.getDocumentByIds(ids).stream()
        .map(Document::getId)
        .toList();

    vectorService.delete(existing);

    List<Document> documents = items.getItems().stream()
        .map(item -> new Document(
            item.id().toString(),
            item.content(),
            item.metadata()
        ))
        .toList();
    vectorService.save(documents);
  }
}

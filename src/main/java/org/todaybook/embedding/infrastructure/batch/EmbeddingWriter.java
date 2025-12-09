package org.todaybook.embedding.infrastructure.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.opensearch.service.OpensearchService;
import org.todaybook.embedding.infrastructure.vector.service.VectorService;
import org.todaybook.embedding.domain.VectorBook;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingWriter implements ItemWriter<VectorBook> {

  private final VectorService vectorService;
  private final OpensearchService opensearchService;

  @Override
  public void write(Chunk<? extends VectorBook> items) {
    List<String> ids = items.getItems().stream()
        .map(item -> item.id().toString())
        .toList();

    List<String> existing = opensearchService.getDocumentByIds(ids).stream()
        .map(Document::getId)
        .toList();

    if (!existing.isEmpty()) vectorService.delete(existing);

    List<Document> documents = items.getItems().stream()
        .map(item -> new Document(
            item.id().toString(),
            item.content(),
            item.metadata()
        ))
        .toList();

    vectorService.save(documents);

    log.debug("[TODAY-BOOK] EmbeddingWriter 실행 - {}권 저장", documents.size());
  }
}

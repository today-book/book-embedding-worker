package org.todaybook.embedding.application.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.domain.VectorBook;
import org.todaybook.embedding.infrastructure.vectorstore.service.VectorStoreService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingWriter implements ItemWriter<VectorBook> {

  private final VectorStoreService vectorStoreService;

  @Override
  public void write(Chunk<? extends VectorBook> items) {
    List<Document> documents =
        items.getItems().stream()
            .map(item -> new Document(item.id().toString(), item.content(), item.metadata()))
            .toList();

    vectorStoreService.save(documents);

    log.debug("[TODAY-BOOK] EmbeddingWriter 실행 - {}권 저장", documents.size());

    try {
      Thread.sleep(60_000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

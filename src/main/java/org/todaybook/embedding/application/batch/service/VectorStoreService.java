package org.todaybook.embedding.application.batch.service;

import java.util.List;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;

public interface VectorStoreService {
  void upsert(String id, EmbeddingDocument document);

  void upsert(List<EmbeddingDocument> documents);
}

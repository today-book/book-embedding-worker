package org.todaybook.embedding.infrastructure.ai.service;

import java.util.List;
import org.springframework.ai.document.Document;

public interface EmbeddingService {
  void save(List<Document> document);
  void delete(List<String> ids);
}

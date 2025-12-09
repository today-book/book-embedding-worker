package org.todaybook.embedding.infrastructure.vector.service;

import java.util.List;
import java.util.Optional;
import org.springframework.ai.document.Document;

public interface VectorService {
  void save(List<Document> document);
  void delete(List<String> ids);
}

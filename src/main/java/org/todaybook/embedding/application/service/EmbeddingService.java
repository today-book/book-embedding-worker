package org.todaybook.embedding.application.service;

import java.util.List;
import java.util.Optional;
import org.springframework.ai.document.Document;

public interface EmbeddingService {
  Optional<Document> getDocumentById(String id);

  List<Document> getDocumentByIds(List<String> ids);
  //  void upsert(String id, Document document);
  //  void upsert(List<Document> documents);
}

package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import java.util.Optional;
import org.springframework.ai.document.Document;

public interface OpensearchService {
  Optional<Document> getDocumentById(String id);

  List<Document> getDocumentByIds(List<String> ids);
  //  void upsert(String id, Document document);
  //  void upsert(List<Document> documents);
}

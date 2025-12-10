package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import org.springframework.ai.document.Document;

public interface OpensearchUpsertService {
  void upsert(String id, Document document);

  void upsert(List<Document> documents);
}

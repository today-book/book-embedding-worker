package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import java.util.Optional;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;

public interface OpensearchQueryService {
  Optional<EmbeddingDocument> getDocumentById(String id);

  List<EmbeddingDocument> getDocumentByIds(List<String> ids);
}

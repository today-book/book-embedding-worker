package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchInvalidDocumentException;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchNullParameterException;

@Slf4j
@Component
public class OpenSearchMapper {
  public EmbeddingDocument toDocument(String id, Map<String, Object> source) {
    if (source == null) {
      throw new OpensearchInvalidDocumentException(
          String.format("[TODAY-BOOK] OpenSearch source가 null입니다. (id=%s)", id));
    }

    String content = getContent(source.get("content"));
    Map<String, Object> metadata = getMetadata(source.get("metadata"));
    float[] embedding = (float[]) source.get("embedding");

    return EmbeddingDocument.of(UUID.fromString(id), content, metadata, embedding);
  }

  public Map<String, Object> toMap(EmbeddingDocument document) {
    if (document == null) {
      throw new OpensearchNullParameterException("document");
    }

    return Map.of(
        "content", document.content(),
        "metadata", document.metadata(),
        "embedding", document.embedding());
  }

  private String getContent(Object value) {
    if (!(value instanceof String content)) {
      throw new OpensearchInvalidDocumentException("[TODAY-BOOK] content 필드가 String 타입이 아닙니다.");
    }

    if (content.isBlank()) {
      throw new OpensearchInvalidDocumentException("[TODAY-BOOK] content 필드가 비어있습니다.");
    }

    return content;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getMetadata(Object value) {
    if (value == null) return Map.of();

    if (value instanceof Map<?, ?>) {
      try {
        return (Map<String, Object>) value;
      } catch (ClassCastException e) {
        throw new OpensearchInvalidDocumentException(
            "[TODAY-BOOK] metadata Map의 키 또는 값 타입이 잘못되었습니다.");
      }
    }
    throw new OpensearchInvalidDocumentException("[TODAY-BOOK] metadata가 Map 타입이 아닙니다.");
  }
}

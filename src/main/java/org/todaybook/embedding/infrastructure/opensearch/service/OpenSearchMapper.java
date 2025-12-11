package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchInvalidDocumentException;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchNullParameterException;

@Slf4j
@Component
public class OpenSearchMapper {
  public Document toDocument(String id, Map<String, Object> source) {
    if (source == null) {
      throw new OpensearchInvalidDocumentException(
          String.format("[TODAY-BOOK] OpenSearch source가 null입니다. (id=%s)", id));
    }

    String content = getContent(source.get("content"));
    Map<String, Object> metadata = getMetadata(source.get("metadata"));

    return new Document(id, content, metadata);
  }

  public Map<String, Object> fromDocument(Document document) {
    if (document == null) {
      throw new OpensearchNullParameterException("document");
    }

    return Map.of(
        "id", document.getId(),
        "content", getContent(document.getText()),
        "metadata", document.getMetadata());
  }

  private String getContent(Object content) {
    if (!(content instanceof String)) {
      throw new OpensearchInvalidDocumentException("[TODAY-BOOK] content 필드가 String 타입이 아닙니다.");
    }

    String contentStr = (String) content;
    if (contentStr.isBlank()) {
      throw new OpensearchInvalidDocumentException("[TODAY-BOOK] content 필드가 비어있습니다.");
    }

    return contentStr;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getMetadata(Object metadata) {
    if (metadata == null) return Map.of();

    if (metadata instanceof Map<?, ?>) {
      try {
        Map<String, Object> metadataMap = (Map<String, Object>) metadata;
      } catch (ClassCastException e) {
        throw new OpensearchInvalidDocumentException(
            "[TODAY-BOOK] metadata Map의 키 또는 값 타입이 잘못되었습니다.");
      }
    }

    throw new OpensearchInvalidDocumentException("[TODAY-BOOK] metadata가 Map 타입이 아닙니다.");
  }
}

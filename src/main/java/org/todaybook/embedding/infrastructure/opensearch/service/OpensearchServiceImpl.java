package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.MgetResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchInternalServerException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpensearchServiceImpl implements OpensearchService {

  @Value("${spring.ai.vectorstore.opensearch.index-name}")
  private String index;

  private final OpenSearchClient client;

  @Override
  public Optional<Document> getDocumentById(String id) {
    try {
      GetResponse<Map> res = client.get(g -> g.index(index).id(id), Map.class);

      return Optional.ofNullable(res)
          .filter(GetResponse::found)
          .map(GetResponse::source)
          .map(source -> toDocument(id, source));
    } catch (Exception e) {
      throw new OpensearchInternalServerException(
          String.format("OpenSearch 단건 조회 실패 (id=%s, exception=%s)", id, e.getMessage()));
    }
  }

  @Override
  public List<Document> getDocumentByIds(List<String> ids) {
    try {
      MgetResponse<Map> res = client.mget(g -> g.index(index).ids(ids), Map.class);

      return res.docs().stream()
          .filter(doc -> doc.isResult() && doc.result().found())
          .map(doc -> toDocument(doc.result().id(), doc.result().source()))
          .toList();
    } catch (Exception e) {
      throw new OpensearchInternalServerException(
          String.format(
              "OpenSearch 목록 조회 실패 (ids=%s, exception=%s)", ids.toString(), e.getMessage()));
    }
  }

  private Document toDocument(String id, Map<String, Object> source) {
    if (source == null) {
      log.warn("[TODAY-BOOK] OpenSearch source가 null입니다. (id={})", id);
      return new Document(id, "", Map.of());
    }

    String content = (String) source.getOrDefault("content", "");
    Map<String, Object> metadata = castMetadata(source.get("metadata"));

    return new Document(id, content, metadata);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> castMetadata(Object metadata) {
    if (metadata instanceof Map<?, ?> map) {
      return (Map<String, Object>) map;
    }
    return Map.of();
  }
}

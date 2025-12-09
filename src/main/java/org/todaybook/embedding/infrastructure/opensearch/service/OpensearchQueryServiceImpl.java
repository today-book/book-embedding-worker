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
public class OpensearchQueryServiceImpl implements OpensearchQueryService {

  @Value("${spring.ai.vectorstore.opensearch.index-name}")
  private String index;

  private final OpenSearchClient client;
  private final OpenSearchMapper mapper;

  @Override
  public Optional<Document> getDocumentById(String id) {
    try {
      GetResponse<Map> res = client.get(g -> g.index(index).id(id), Map.class);

      return Optional.ofNullable(res)
          .filter(GetResponse::found)
          .map(GetResponse::source)
          .map(source -> mapper.toDocument(id, source));
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
          .map(doc -> mapper.toDocument(doc.result().id(), doc.result().source()))
          .toList();
    } catch (Exception e) {
      throw new OpensearchInternalServerException(
          String.format(
              "OpenSearch 목록 조회 실패 (ids=%s, exception=%s)", ids.toString(), e.getMessage()));
    }
  }
}

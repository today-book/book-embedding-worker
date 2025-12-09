package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.MgetResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
      if (!res.found())
        return Optional.empty();

      Map<String, Object> source = res.source();
      if (source == null) return Optional.empty();

      String content = (String) source.get("content");
      Map<String, Object> metadata = (Map<String, Object>) source.get("metadata");

      return Optional.of(new Document(id, content,  metadata));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Document> getDocumentByIds(List<String> ids) {
    try {
      MgetResponse<Map> res = client.mget(g -> g.index(index).ids(ids), Map.class);

      return res.docs().stream()
          .filter(doc -> doc.isResult() && doc.result().found())
          .map(doc -> {
            Map<String, Object> source = doc.result().source();

            if (source == null) return null;

            String content = (String) source.get("content");
            Map<String, Object> metadata = (Map<String, Object>) source.get("metadata");

            return new Document(doc.result().id(), content,  metadata);
          })
          .toList();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

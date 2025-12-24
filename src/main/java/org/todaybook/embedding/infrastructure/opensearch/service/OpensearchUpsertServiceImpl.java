package org.todaybook.embedding.infrastructure.opensearch.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.todaybook.embedding.application.batch.service.VectorStoreService;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;
import org.todaybook.embedding.infrastructure.opensearch.exception.OpensearchInternalServerException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpensearchUpsertServiceImpl implements VectorStoreService {

  @Value("${spring.ai.vectorstore.opensearch.index-name}")
  private String index;

  private final OpenSearchClient client;
  private final OpenSearchMapper mapper;

  @Override
  public void upsert(String id, EmbeddingDocument document) {
    try {
      Map<String, Object> doc = mapper.toMap(document);

      client.update(u -> u.index(index).id(id).doc(doc).docAsUpsert(true), Map.class);
    } catch (Exception e) {
      throw new OpensearchInternalServerException(
          String.format("OpenSearch 단건 수정 실패 (id=%s, exception=%s)", id, e.getMessage()));
    }
  }

  @Override
  public void upsert(List<EmbeddingDocument> documents) {
    try {
      BulkResponse response =
          client.bulk(
              b -> {
                for (EmbeddingDocument document : documents) {
                  String id = document.id().toString();
                  Map<String, Object> doc = mapper.toMap(document);

                  b.operations(
                      op -> op.update(u -> u.index(index).id(id).document(doc).docAsUpsert(true)));
                }
                return b;
              });

      if (response.errors()) {
        response
            .items()
            .forEach(
                item -> {
                  if (item.error() != null) {
                    log.warn(
                        "[TODAY-BOOK] OpenSearch Bulk 수정 일부 실패 (id={}, reason={})",
                        item.id(),
                        item.error());
                  }
                });
      }
    } catch (Exception e) {
      throw new OpensearchInternalServerException(
          String.format("OpenSearch bulk 수정 실패 (exception=%s)", e.getMessage()));
    }
  }
}

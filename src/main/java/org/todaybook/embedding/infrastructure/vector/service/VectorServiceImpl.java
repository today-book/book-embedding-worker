package org.todaybook.embedding.infrastructure.vector.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorServiceImpl implements VectorService {

  private final VectorStore vectorStore;

  @Override
  public void save(List<Document> document) {
    vectorStore.add(document);
  }

  @Override
  public void delete(List<String> ids) {
    vectorStore.delete(ids);
  }
}

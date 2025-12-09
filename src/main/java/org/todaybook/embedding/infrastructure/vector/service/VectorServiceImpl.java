package org.todaybook.embedding.infrastructure.vector.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
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

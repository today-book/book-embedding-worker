package org.todaybook.embedding.infrastructure.embedding.strategy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenBatchStrategy {

  private final BatchingStrategy batchingStrategy;

  public List<List<Document>> split(List<Document> documents) {
    return batchingStrategy.batch(documents);
  }
}

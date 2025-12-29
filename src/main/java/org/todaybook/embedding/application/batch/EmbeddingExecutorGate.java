package org.todaybook.embedding.application.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.application.batch.service.EmbeddingService;
import org.todaybook.embedding.infrastructure.embedding.limiter.ConcurrencyLimiter;
import org.todaybook.embedding.infrastructure.embedding.limiter.TokenEstimateLimiter;

@Component
@RequiredArgsConstructor
public class EmbeddingExecutorGate {

  private final TokenEstimateLimiter tokenEstimateLimiter;
  private final ConcurrencyLimiter concurrencyLimiter;

  private final EmbeddingService embeddingService;

  public List<float[]> embed(List<String> texts) {
    texts.forEach(tokenEstimateLimiter::check);

    return concurrencyLimiter.execute(() -> embeddingService.embed(texts));
  }
}

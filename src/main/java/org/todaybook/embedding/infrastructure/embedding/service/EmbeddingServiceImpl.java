package org.todaybook.embedding.infrastructure.embedding.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.todaybook.embedding.application.batch.service.EmbeddingService;
import org.todaybook.embedding.infrastructure.embedding.limiter.ConcurrencyLimiter;
import org.todaybook.embedding.infrastructure.embedding.limiter.TokenEstimateLimiter;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

  private final TokenEstimateLimiter tokenEstimateLimiter;
  private final ConcurrencyLimiter concurrencyLimiter;

  private final EmbeddingModel model;

  @Override
  @RateLimiter(name = "embeddingRateLimiter")
  public List<float[]> embed(List<String> texts) {

    texts.forEach(tokenEstimateLimiter::check);

    return concurrencyLimiter.execute(() -> model.embed(texts));
  }
}

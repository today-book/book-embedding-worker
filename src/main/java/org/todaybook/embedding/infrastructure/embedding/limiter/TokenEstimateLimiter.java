package org.todaybook.embedding.infrastructure.embedding.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.embedding.TokenLimits;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenEstimateLimiter {

  private final TokenCountEstimator tokenEstimator;

  public void check(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Text is null");
    }

    int tokens = tokenEstimator.estimate(text);

    if (tokens > TokenLimits.SAFE_TOKEN_LIMIT) {
      throw new IllegalArgumentException(
          String.format("Token limit exceeded (tokens=%d, length=%d)", tokens, text.length()));
    }
  }
}

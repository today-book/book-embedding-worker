package org.todaybook.embedding.infrastructure.embedding.strategy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.embedding.TokenLimits;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenSingleStrategy {

  private final TokenCountEstimator tokenEstimator;

  public List<Document> filter(List<Document> documents) {
    return documents.stream().filter(this::isValid).toList();
  }

  private boolean isValid(Document document) {
    int tokens = tokenEstimator.estimate(document.getFormattedContent());
    boolean valid = tokens <= TokenLimits.VALID_TOKEN_LIMIT;

    if (!valid) {
      log.debug(
          "[TODAY-BOOK] Document invalid by token. (tokens={}, length={})",
          tokens,
          document.getFormattedContent().length());
    }

    return valid;
  }
}

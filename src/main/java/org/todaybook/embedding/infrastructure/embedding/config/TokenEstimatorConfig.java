package org.todaybook.embedding.infrastructure.embedding.config;

import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenEstimatorConfig {

  @Bean
  public TokenCountEstimator tokenCountEstimator() {
    return new JTokkitTokenCountEstimator(EncodingType.CL100K_BASE);
  }
}

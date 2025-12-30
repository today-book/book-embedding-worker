package org.todaybook.embedding.infrastructure.embedding.config;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.ResourceExhaustedException;
import com.knuddels.jtokkit.api.EncodingType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(VertexEmbeddingProperties.class)
public class VertexEmbeddingConfig {

  private final VertexEmbeddingProperties properties;

  @Bean
  public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() {
    return VertexAiEmbeddingConnectionDetails.builder()
        .projectId(properties.projectId())
        .location(properties.location())
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingOptions embeddingOptions() {
    return VertexAiTextEmbeddingOptions.builder()
        .model(properties.model())
        .autoTruncate(false)
        .dimensions(768)
        .build();
  }

  @Bean
  public RetryTemplate embeddingRetryTemplate() {
    return RetryTemplate.builder()
        .notRetryOn(InvalidArgumentException.class)
        .notRetryOn(ResourceExhaustedException.class)
        .maxAttempts(1)
        .build();
  }

  @Bean
  public BatchingStrategy embeddingBatchingStrategy() {
    return new TokenCountBatchingStrategy(EncodingType.CL100K_BASE, 7000, 0.2);
  }

  @Bean
  public VertexAiTextEmbeddingModel embeddingModel(
      VertexAiEmbeddingConnectionDetails connectionDetails,
      VertexAiTextEmbeddingOptions embeddingOptions,
      RetryTemplate embeddingRetryTemplate) {
    return new VertexAiTextEmbeddingModel(
        connectionDetails, embeddingOptions, embeddingRetryTemplate);
  }
}

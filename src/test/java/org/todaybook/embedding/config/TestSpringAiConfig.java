package org.todaybook.embedding.config;

import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSpringAiConfig {

  @Bean
  public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() {
    return VertexAiEmbeddingConnectionDetails.builder()
        .projectId("todaybook-479908")
        .location("us-central1")
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingOptions embeddingOptions() {
    return VertexAiTextEmbeddingOptions.builder()
        .model("gemini-embedding-001")
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingModel embeddingModel(
      VertexAiEmbeddingConnectionDetails connectionDetails,
      VertexAiTextEmbeddingOptions embeddingOptions
  ) {
    return new VertexAiTextEmbeddingModel(connectionDetails, embeddingOptions);
  }
}

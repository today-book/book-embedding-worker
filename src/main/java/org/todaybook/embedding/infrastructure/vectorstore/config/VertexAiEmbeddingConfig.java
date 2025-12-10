package org.todaybook.embedding.infrastructure.vectorstore.config;

import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertexAiEmbeddingConfig {
  @Bean
  public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() {
    return VertexAiEmbeddingConnectionDetails.builder()
        .projectId("todaybook-479908")
        .location("us-central1")
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingOptions embeddingOptions() {
    return VertexAiTextEmbeddingOptions.builder().model("gemini-embedding-001").build();
  }

  @Bean
  public VertexAiTextEmbeddingModel embeddingModel(
      VertexAiEmbeddingConnectionDetails connectionDetails,
      VertexAiTextEmbeddingOptions embeddingOptions) {
    return new VertexAiTextEmbeddingModel(connectionDetails, embeddingOptions);
  }
}

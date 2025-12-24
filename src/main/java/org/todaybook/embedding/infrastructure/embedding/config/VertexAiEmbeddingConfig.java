package org.todaybook.embedding.infrastructure.embedding.config;

import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertexAiEmbeddingConfig {

  @Value("${spring.ai.vertex.ai.embedding.project-id}")
  private String projectId;

  @Value("${spring.ai.vertex.ai.embedding.location}")
  private String location;

  @Bean
  public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() {
    return VertexAiEmbeddingConnectionDetails.builder()
        .projectId(projectId)
        .location(location)
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingOptions embeddingOptions() {
    return VertexAiTextEmbeddingOptions.builder()
        .model("gemini-embedding-001")
        .autoTruncate(false)
        .dimensions(768)
        .build();
  }

  @Bean
  public VertexAiTextEmbeddingModel embeddingModel(
      VertexAiEmbeddingConnectionDetails connectionDetails,
      VertexAiTextEmbeddingOptions embeddingOptions) {
    return new VertexAiTextEmbeddingModel(connectionDetails, embeddingOptions);
  }
}

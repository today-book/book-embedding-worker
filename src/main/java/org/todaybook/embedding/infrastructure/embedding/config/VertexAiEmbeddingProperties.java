package org.todaybook.embedding.infrastructure.embedding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.vertex.ai.embedding")
public record VertexAiEmbeddingProperties(
    String projectId,
    String location
) {}

package org.todaybook.embedding.infrastructure.embedding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.gcp")
public record VertexEmbeddingProperties(String projectId, String location, String model) {}

package org.todaybook.embedding.infrastructure.ai.dto;

import java.util.Map;

public record VectorBook(
    String id,
    String content,
    Map<String, Object> metadata
) {}

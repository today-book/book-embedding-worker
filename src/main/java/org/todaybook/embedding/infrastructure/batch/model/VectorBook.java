package org.todaybook.embedding.infrastructure.batch.model;

import java.util.Map;
import java.util.UUID;

public record VectorBook(UUID id, String content, Map<String, Object> metadata) {}

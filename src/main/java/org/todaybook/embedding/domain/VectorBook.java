package org.todaybook.embedding.domain;

import java.util.Map;
import java.util.UUID;

public record VectorBook(UUID id, String content, Map<String, Object> metadata) {}

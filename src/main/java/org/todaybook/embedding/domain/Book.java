package org.todaybook.embedding.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Book(
    UUID id,
    String isbn,
    String title,
    List<String> categories,
    String description,
    String author,
    String publisher,
    LocalDate publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

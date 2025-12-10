package org.todaybook.embedding.application.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.VectorBook;

class EmbeddingProcessorTests {

  private final EmbeddingProcessor processor = new EmbeddingProcessor();

  @Test
  @DisplayName("Embedding Processor 테스트: Book -> VectorBook 변환")
  void test1() {
    Book book =
        new Book(
            UUID.randomUUID(),
            "0000000000001",
            "도서 제목",
            List.of("소설"),
            "도서 소개 입니다",
            "도서 저자",
            "출판사",
            LocalDate.now(),
            LocalDateTime.now(),
            LocalDateTime.now());

    VectorBook result = processor.process(book);

    assertNotNull(result);
    assertEquals(book.id(), result.id());

    // content 검증
    assertThat(result.content())
        .contains("title: 도서 제목")
        .contains("author: 도서 저자")
        .contains("description: 도서 소개 입니다")
        .contains("categories: 소설");

    // metadata 검증
    Map<String, Object> metadata = result.metadata();
    assertThat(metadata).containsEntry("title", "도서 제목");
    assertThat(metadata).containsEntry("author", "도서 저자");
    assertThat(metadata.get("categories")).isEqualTo(List.of("소설"));
  }
}

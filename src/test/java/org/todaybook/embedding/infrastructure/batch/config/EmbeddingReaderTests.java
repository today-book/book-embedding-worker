package org.todaybook.embedding.infrastructure.batch.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.todaybook.embedding.config.TestContainersConfig;
import org.todaybook.embedding.config.TestSpringAiConfig;
import org.todaybook.embedding.domain.Book;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestContainersConfig.class, TestSpringAiConfig.class})
@Sql({"/org/springframework/batch/core/schema-postgresql.sql", "classpath:sql/init.sql", "classpath:sql/book-data.sql"})
class EmbeddingReaderTests {

  @Autowired
  private JdbcPagingItemReader<Book> reader;

  @BeforeEach
  void setup() throws Exception {
    StepExecution step = new StepExecution("step", new JobExecution(1L));
    StepSynchronizationManager.register(step);

    reader.afterPropertiesSet();
  }

  @AfterEach
  void cleanup() {
    StepSynchronizationManager.close();
  }

  @Test
  @DisplayName("Spring Batch Reader 테스트")
  void test1() throws Exception {
    Book book;

    int count = 0;
    while ((book = reader.read()) != null) {
      System.out.println("Book: " + book);
      count++;
    }

    assertEquals(10, count);
  }
}
package org.todaybook.embedding.infrastructure.batch.reader;

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
import org.todaybook.embedding.config.TestPostgresContainerConfig;
import org.todaybook.embedding.infrastructure.batch.model.Book;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestPostgresContainerConfig.class})
@Sql({
  "/org/springframework/batch/core/schema-postgresql.sql",
  "classpath:sql/init.sql",
  "classpath:sql/book-data.sql"
})
class PagingItemReaderTests {

  @Autowired private JdbcPagingItemReader<Book> reader;

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

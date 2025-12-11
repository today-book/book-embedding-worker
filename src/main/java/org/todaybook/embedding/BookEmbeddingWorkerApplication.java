package org.todaybook.embedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BookEmbeddingWorkerApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookEmbeddingWorkerApplication.class, args);
  }
}

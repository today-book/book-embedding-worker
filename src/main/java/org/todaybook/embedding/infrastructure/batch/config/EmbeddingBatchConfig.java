package org.todaybook.embedding.infrastructure.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.VectorBook;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class EmbeddingBatchConfig {

  private final JobRepository repository;
  private final PlatformTransactionManager transactionManager;
  private final JdbcPagingItemReader<Book> reader;
  private final EmbeddingProcessor processor;
  private final EmbeddingWriter writer;

  @Bean
  public Job embeddingJob() {
    return new JobBuilder("embeddingJob", repository)
        .start(embeddingStep())
        .build();
  }

  @Bean
  public Step embeddingStep() {
    return new StepBuilder("embeddingStep", repository)
        .<Book, VectorBook>chunk(100, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant() // 실패 시 Chunk 단위 재시도
        .retry(Exception.class)
        .retryLimit(3)
        .build();
  }
}

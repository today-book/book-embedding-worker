package org.todaybook.embedding.infrastructure.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.todaybook.embedding.infrastructure.batch.model.Book;
import org.todaybook.embedding.infrastructure.batch.model.VectorBook;
import org.todaybook.embedding.infrastructure.batch.processor.EmbeddingProcessor;
import org.todaybook.embedding.infrastructure.batch.reader.KeysetItemReader;
import org.todaybook.embedding.infrastructure.batch.writer.EmbeddingWriter;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class EmbeddingStepConfig {

  private final JobRepository repository;

  @Bean
  @JobScope
  public Step embeddingStep(
      PlatformTransactionManager transactionManager,
      KeysetItemReader reader,
      EmbeddingProcessor processor,
      EmbeddingWriter writer,
      @Value("#{jobParameters['chunkSize'] ?: 50}") int chunkSize) {
    return new StepBuilder("embeddingStep", repository)
        .<Book, VectorBook>chunk(chunkSize, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skip(IllegalArgumentException.class)
        .skipLimit(Integer.MAX_VALUE)
        .build();
  }
}

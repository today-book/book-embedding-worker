package org.todaybook.embedding.application.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class EmbeddingJobConfig {

  private final JobRepository repository;
  private final Step embeddingStep;

  @Bean
  public Job embeddingJob() {
    return new JobBuilder("embeddingJob", repository).start(embeddingStep).build();
  }
}

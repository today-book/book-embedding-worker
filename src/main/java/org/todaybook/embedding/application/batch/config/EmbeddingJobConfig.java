package org.todaybook.embedding.application.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.todaybook.embedding.application.batch.listener.EmbeddingJobListener;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class EmbeddingJobConfig {

  private final JobRepository repository;

  @Bean
  public Job embeddingJob(Step embeddingStep, EmbeddingJobListener jobListener) {
    return new JobBuilder("embeddingJob", repository)
        .start(embeddingStep)
        .listener(jobListener)
        .build();
  }
}

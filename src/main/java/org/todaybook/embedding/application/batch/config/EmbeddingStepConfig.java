package org.todaybook.embedding.application.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.todaybook.embedding.application.batch.tasklet.EmbeddingTasklet;

@Configuration
@RequiredArgsConstructor
public class EmbeddingStepConfig {

  private final JobRepository repository;

  @Bean
  public Step embeddingStep(EmbeddingTasklet tasklet) {
    return new StepBuilder("embedding", repository)
        .tasklet(tasklet, new ResourcelessTransactionManager())
        .build();
  }
}

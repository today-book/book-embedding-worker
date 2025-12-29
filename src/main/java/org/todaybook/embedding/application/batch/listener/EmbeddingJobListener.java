package org.todaybook.embedding.application.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.embedding.EmbeddingExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingJobListener implements JobExecutionListener {

  private final EmbeddingExecutor embeddingExecutor;

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info(
        "[TODAY-BOOK] Job finished (job={}, status={}), shutdown embedding executor",
        jobExecution.getJobInstance().getJobName(),
        jobExecution.getStatus());
    embeddingExecutor.shutdown();
  }
}

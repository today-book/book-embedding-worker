package org.todaybook.embedding.infrastructure.batch.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobExecutionServiceImpl implements JobExecutionService {

  private final JobRepository repository;

  @Override
  public LocalDateTime getLastExecutionTime(String job) {
    JobParameters params = new JobParameters();

    JobExecution execution = repository.getLastJobExecution(job, params);
    if (execution == null) return null;
    if (execution.getEndTime() == null) return null;
    return execution.getEndTime();
  }
}

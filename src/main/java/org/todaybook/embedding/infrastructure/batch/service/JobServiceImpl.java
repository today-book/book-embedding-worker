package org.todaybook.embedding.infrastructure.batch.service;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final JobExplorer jobExplorer;

  @Override
  public boolean isRunning(String job) {
    return !jobExplorer.findRunningJobExecutions(job).isEmpty();
  }

  @Override
  public void terminate(String job) {
    Set<JobExecution> jobs = jobExplorer.findRunningJobExecutions(job);

    for (JobExecution execution : jobs) {
      execution.setStatus(BatchStatus.FAILED);
      execution.setExitStatus(ExitStatus.FAILED);
      execution.setEndTime(LocalDateTime.now());
      jobRepository.update(execution);

      log.debug("[TODAY-BOOK] JobExecution을 강제 종료합니다. (executionId={})", execution.getId());
    }
  }
}

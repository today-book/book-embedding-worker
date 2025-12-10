package org.todaybook.embedding.application.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class EmbeddingScheduler {

  private final JobLauncher jobLauncher;
  private final Job embeddingJob;

  @Scheduled(cron = "0 0 4 * * *")
  public void schedule() {
    JobParameters params =
        new JobParametersBuilder().addLong("runTime", System.currentTimeMillis()).toJobParameters();

    try {
      jobLauncher.run(embeddingJob, params);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

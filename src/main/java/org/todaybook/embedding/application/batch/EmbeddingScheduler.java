package org.todaybook.embedding.application.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingScheduler {

  private final JobLauncher jobLauncher;
  private final Job embeddingJob;

  @EventListener(ApplicationReadyEvent.class)
  public void runOnceOnStart() {
    log.info("[TODAY-BOOK] 최초 임베딩 작업을 시작합니다.");
    run();
  }

  @Scheduled(cron = "0 0 4 * * *")
  public void run() {
    try {
      JobParameters params =
          new JobParametersBuilder().addLong("run", System.currentTimeMillis()).toJobParameters();

      jobLauncher.run(embeddingJob, params);

      log.info("[TODAY-BOOK] 임베딩 작업을 시작합니다. (run={})", params.getLong("run"));
    } catch (Exception e) {
      log.error("[TODAY-BOOK] 임베딩 작업에 실패하였습니다. (message={})", e.getMessage());
    }
  }
}

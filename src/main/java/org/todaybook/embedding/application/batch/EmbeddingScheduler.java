package org.todaybook.embedding.application.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.application.batch.service.JobService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingScheduler {

  private final JobService jobService;
  private final JobLauncher jobLauncher;
  private final Job embeddingJob;

  @EventListener(ApplicationReadyEvent.class)
  public void applicationReady() {
    log.info("[TODAY-BOOK] 애플리케이션 시작: 이전 실행 중 배치 작업을 종료합니다.");
    jobService.terminate(embeddingJob.getName());

    log.info("[TODAY-BOOK] 최초 배치 작업을 시작합니다. (job={})", embeddingJob.getName());
    run();
  }

  @EventListener(ContextClosedEvent.class)
  public void applicationShutdown() {
    log.warn("[TODAY-BOOK] 애플리케이션 정상 종료: 실행 중인 배치를 종료합니다.");
    jobService.terminate(embeddingJob.getName());
  }

  @EventListener(ApplicationFailedEvent.class)
  public void applicationFailed() {
    log.error("[TODAY-BOOK] 애플리케이션 비정상 종료: 실행 중인 배치를 종료합니다.");
    jobService.terminate(embeddingJob.getName());
  }

  @Scheduled(cron = "0 0 4 * * *")
  public void run() {
    try {
      if (jobService.isRunning(embeddingJob.getName())) {
        log.warn("[TODAY-BOOK] 이전 임베딩 작업이 아직 실행 중입니다. 이번 실행은 건너뜁니다.");
        return;
      }

      JobParameters params =
          new JobParametersBuilder().addLong("run", System.currentTimeMillis()).toJobParameters();

      jobLauncher.run(embeddingJob, params);

      log.info("[TODAY-BOOK] 임베딩 작업을 시작합니다. (run={})", params.getLong("run"));
    } catch (Exception e) {
      log.error("[TODAY-BOOK] 임베딩 작업에 실패하였습니다. (message={})", e.getMessage());
    }
  }
}

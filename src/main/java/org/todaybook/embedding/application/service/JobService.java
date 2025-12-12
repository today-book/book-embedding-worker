package org.todaybook.embedding.application.service;

import java.time.LocalDateTime;

public interface JobService {
  LocalDateTime getLastSuccessTime(String job);

  boolean isRunning(String job);

  void terminate(String job);
}

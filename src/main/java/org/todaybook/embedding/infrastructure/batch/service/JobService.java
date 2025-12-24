package org.todaybook.embedding.infrastructure.batch.service;

public interface JobService {
  boolean isRunning(String job);

  void terminate(String job);
}

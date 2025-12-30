package org.todaybook.embedding.application.batch.service;

public interface JobService {
  boolean isRunning(String job);

  void terminate(String job);
}

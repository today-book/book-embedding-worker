package org.todaybook.embedding.infrastructure.batch.service;

import java.time.LocalDateTime;

public interface JobExecutionService {
  LocalDateTime getLastExecutionTime(String job);
}

package org.todaybook.embedding.application.service;

import java.time.LocalDateTime;

public interface JobExecutionService {
  LocalDateTime getLastExecutionTime(String job);
}

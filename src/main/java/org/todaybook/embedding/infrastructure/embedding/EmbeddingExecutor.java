package org.todaybook.embedding.infrastructure.embedding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmbeddingExecutor {

  private ExecutorService executor;

  public synchronized ExecutorService get() {
    if (executor == null || executor.isShutdown()) {
      log.info("[TODAY-BOOK] Embedding Executor created");
      executor =
          Executors.newSingleThreadExecutor(
              r -> {
                Thread t = new Thread(r);
                t.setName("embedding-executor");
                return t;
              });
    }
    return executor;
  }

  public synchronized void shutdown() {
    if (executor != null) {
      log.info("[TODAY-BOOK] Embedding Executor shutdown");
      executor.shutdown();
      executor = null;
    }
  }
}

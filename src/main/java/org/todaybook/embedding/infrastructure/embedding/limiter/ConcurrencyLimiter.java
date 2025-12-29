package org.todaybook.embedding.infrastructure.embedding.limiter;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ConcurrencyLimiter {

  private final Semaphore semaphore = new Semaphore(1);

  public <T> T execute(Supplier<T> task) {
    if (!semaphore.tryAcquire()) {
      throw new IllegalStateException("Embedding concurrency limited");
    }

    try {
      return task.get();
    } finally {
      semaphore.release();
    }
  }
}

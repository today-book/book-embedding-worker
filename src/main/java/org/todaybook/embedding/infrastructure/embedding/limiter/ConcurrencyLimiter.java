package org.todaybook.embedding.infrastructure.embedding.limiter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.infrastructure.embedding.EmbeddingExecutor;

@Component
@RequiredArgsConstructor
public class ConcurrencyLimiter {

  private final EmbeddingExecutor embeddingExecutor;
  private final Semaphore semaphore = new Semaphore(1);

  public <T> T execute(Supplier<T> task) {
    if (!semaphore.tryAcquire()) {
      throw new IllegalStateException("Embedding concurrency limited");
    }

    try {
      ExecutorService executor = embeddingExecutor.get();
      return CompletableFuture.supplyAsync(task, executor).join();
    } finally {
      semaphore.release();
    }
  }
}

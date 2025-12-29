package org.todaybook.embedding.application.batch.service;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.ResourceExhaustedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.todaybook.embedding.application.batch.EmbeddingExecutorGate;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.infrastructure.embedding.strategy.TokenBatchStrategy;
import org.todaybook.embedding.infrastructure.embedding.strategy.TokenSingleStrategy;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingBatchServiceImpl implements EmbeddingBatchService {

  private final TokenSingleStrategy tokenSingleStrategy;
  private final TokenBatchStrategy tokenBatchStrategy;

  private final EmbeddingExecutorGate embeddingExecutorGate;
  private final VectorStoreService vectorStoreService;

  @Override
  public void save(List<Book> books) {
    if (books.isEmpty()) return;

    log.info("[TODAY-BOOK] 임베딩 시작 - 대상 도서 {}권", books.size());

    List<Document> source =
        books.stream()
            .map(
                book ->
                    new Document(
                        book.id().toString(),
                        EmbeddingDocument.buildContent(book),
                        EmbeddingDocument.buildMetadata(book)))
            .toList();

    List<Document> documents = tokenSingleStrategy.filter(source);

    List<List<Document>> batches = tokenBatchStrategy.split(documents);

    log.debug("[TODAY-BOOK] 토큰 기준 배치 분할 - {} batches", batches.size());

    List<EmbeddingDocument> result = new ArrayList<>();

    int index = 1;
    for (List<Document> batch : batches) {
      try {
        log.debug(
            "[TODAY-BOOK] Embedding batch {}/{} - {} docs", index++, batches.size(), batch.size());

        List<float[]> embeddings =
            embeddingExecutorGate.embed(batch.stream().map(Document::getFormattedContent).toList());

        for (int i = 0; i < embeddings.size(); i++) {
          result.add(EmbeddingDocument.from(batch.get(i), embeddings.get(i)));
        }
      } catch (IllegalArgumentException e) {
        log.warn("[TODAY-BOOK] Invalid document skipped. {}", e.getMessage());
      } catch (CompletionException e) {
        handleExecutorException(e);
      } catch (Exception e) {
        log.error("[TODAY-BOOK] Unexpected embedding error.", e);
        throw e;
      }
    }

    if (result.isEmpty()) {
      log.warn("[TODAY-BOOK] 저장할 임베딩 결과가 없습니다.");
      return;
    }

    try {
      vectorStoreService.upsert(result);
      log.info("[TODAY-BOOK] VectorDB 저장 완료 - {}권", result.size());
    } catch (Exception e) {
      log.error("[TODAY-BOOK] VectorDB 저장에 실패하였습니다. Job을 중단합니다.", e);
      throw e;
    }
  }

  private void handleExecutorException(CompletionException e) {
    Throwable cause = e.getCause();

    if (cause instanceof RequestNotPermitted ex) {
      log.error("[TODAY-BOOK] RateLimiter violated");
      throw ex;
    }

    if (cause instanceof ResourceExhaustedException ex) {
      log.error("[TODAY-BOOK] API quota exceeded despite limiter");
      throw ex;
    }

    if (cause instanceof InvalidArgumentException ex) {
      log.error("[TODAY-BOOK] Token validation failed");
      throw ex;
    }

    if (cause instanceof StatusRuntimeException ex) {
      log.error("[TODAY-BOOK] gRPC fatal error. status={}", ex.getStatus(), ex);
      throw ex;
    }

    log.error("[TODAY-BOOK] Unexpected executor error", e);
    throw e;
  }
}

package org.todaybook.embedding.application.batch.service;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.todaybook.embedding.application.batch.dto.EmbeddingDocument;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.infrastructure.embedding.TokenBatchSplitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingBatchServiceImpl implements EmbeddingBatchService {

  private final TokenBatchSplitter tokenBatchSplitter;

  private final EmbeddingService embeddingService;
  private final VectorStoreService vectorStoreService;

  @Override
  public void save(List<Book> books) {
    if (books.isEmpty()) return;

    log.info("[TODAY-BOOK] 임베딩 시작 - 대상 도서 {}권", books.size());

    List<Document> documents =
        books.stream()
            .map(
                book ->
                    new Document(
                        book.id().toString(),
                        EmbeddingDocument.buildContent(book),
                        EmbeddingDocument.buildMetadata(book)))
            .toList();

    List<List<Document>> batches = tokenBatchSplitter.split(documents);

    log.debug("[TODAY-BOOK] 토큰 기준 배치 분할 - {} batches", batches.size());

    List<EmbeddingDocument> result = new ArrayList<>();

    int index = 1;
    for (List<Document> batch : batches) {
      try {
        log.debug(
            "[TODAY-BOOK] Embedding batch {}/{} - {} docs", index++, batches.size(), batch.size());

        List<float[]> embeddings =
            embeddingService.embed(batch.stream().map(Document::getFormattedContent).toList());

        for (int i = 0; i < embeddings.size(); i++) {
          result.add(EmbeddingDocument.from(batch.get(i), embeddings.get(i)));
        }
      } catch (IllegalArgumentException e) {
        log.warn("[TODAY-BOOK] 임베딩에 실패하여 스킵하고 이어서 배치를 진행합니다.");
        log.debug("[TODAY-BOOK] Embedding batch skip (documents={})", batch);
      } catch (RequestNotPermitted e) {
        log.warn("[TODAY-BOOK] Rate limit reached");
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
}

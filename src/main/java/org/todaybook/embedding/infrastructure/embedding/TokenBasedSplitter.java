package org.todaybook.embedding.infrastructure.embedding;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.stereotype.Component;

/**
 * {@code BookSplitter}는 하나의 도서 콘텐츠를 토큰 수 기준으로 안전한 크기의 여러 조각으로 분할한다.
 *
 * <p>분할 전략:
 *
 * <ul>
 *   <li>1차: 문단 단위(\n\n) 분할
 *   <li>2차: 문단이 너무 클 경우 토큰 기준 강제 분할
 * </ul>
 *
 * <p>Vertex AI(gemini-embedding) 환경에서 토큰 초과 및 auto-truncate를 방지하기 위한 용도이다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBasedSplitter {

  /** 권장 chunk 크기 (토큰 기준) */
  private static final int MAX_TOKENS_PER_CHUNK = 700;

  /** 문단이 이 크기를 넘으면 강제 분할 */
  private static final int HARD_LIMIT_TOKENS = 800;

  private final TokenCountEstimator estimator;

  public List<String> split(String content) {
    List<String> chunks = new ArrayList<>();

    StringBuilder current = new StringBuilder();

    String[] paragraphs = content.split("\\n\\n");

    for (String paragraph : paragraphs) {
      paragraph = paragraph.trim();
      if (paragraph.isEmpty()) continue;

      // 문단 하나가 너무 큰 경우 → 강제 토큰 분할
      if (estimate(paragraph) > HARD_LIMIT_TOKENS) {
        flushIfNotEmpty(current, chunks);
        chunks.addAll(splitLargeParagraph(paragraph));
        continue;
      }

      // 현재 chunk에 추가 가능한지 확인
      if (estimate(current + paragraph) > MAX_TOKENS_PER_CHUNK) {
        flushIfNotEmpty(current, chunks);
      }

      current.append(paragraph).append("\n\n");
    }

    flushIfNotEmpty(current, chunks);

    log.debug("[BookSplitter] split into {} chunks", chunks.size());
    return chunks;
  }

  /** 토큰 수 계산 */
  private int estimate(String text) {
    return estimator.estimate(text);
  }

  /** 현재 버퍼를 chunk로 flush */
  private void flushIfNotEmpty(StringBuilder current, List<String> chunks) {
    if (!current.isEmpty()) {
      chunks.add(current.toString().trim());
      current.setLength(0);
    }
  }

  /** 너무 큰 문단을 토큰 기준으로 강제 분할 */
  private List<String> splitLargeParagraph(String paragraph) {
    List<String> result = new ArrayList<>();

    String[] words = paragraph.split("\\s+");
    StringBuilder current = new StringBuilder();

    for (String word : words) {
      if (estimate(current + " " + word) > MAX_TOKENS_PER_CHUNK) {
        result.add(current.toString().trim());
        current.setLength(0);
      }
      current.append(word).append(" ");
    }

    if (!current.isEmpty()) {
      result.add(current.toString().trim());
    }

    return result;
  }
}

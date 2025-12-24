package org.todaybook.embedding.application.batch.service;

import java.util.List;

public interface EmbeddingService {
  float[] embed(String content);
  List<float[]> embed(List<String> contents);
}

package org.todaybook.embedding.application.batch.service;

import java.util.List;

public interface EmbeddingService {
  List<float[]> embed(List<String> texts);
}

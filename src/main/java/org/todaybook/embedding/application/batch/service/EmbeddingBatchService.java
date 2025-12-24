package org.todaybook.embedding.application.batch.service;

import java.util.List;
import org.todaybook.embedding.domain.Book;

public interface EmbeddingBatchService {
  void save(List<Book> books);
}

package org.todaybook.embedding.domain.service;

import java.util.List;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.BookKeyset;

public interface BookQueryService {
  List<Book> findByCursor(BookKeyset keyset, int size);
}

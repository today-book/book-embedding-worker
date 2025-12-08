package org.todaybook.embedding.infrastructure.vector.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorServiceImpl implements VectorService {

  private final VectorStore vectorStore;

  @Override
  public Optional<Document> getDocumentById(String id) {
    SearchRequest request = SearchRequest.builder()
        .query("")
        .filterExpression("bookId == '" + id + "'")
        .build();

    return vectorStore.similaritySearch(request)
        .stream()
        .findFirst();
  }

  @Override
  public List<Document> getDocumentByIds(List<String> ids) {
    String expr = ids.stream()
        .map(id -> "bookId == '" + id + "'")
        .collect(Collectors.joining(" || "));

    SearchRequest request = SearchRequest.builder()
        .query("")
        .filterExpression(expr)
        .build();

    return vectorStore.similaritySearch(request);
  }

  @Override
  public void save(List<Document> document) {
    vectorStore.add(document);
  }

  @Override
  public void delete(List<String> ids) {
    vectorStore.delete(ids);
  }
}

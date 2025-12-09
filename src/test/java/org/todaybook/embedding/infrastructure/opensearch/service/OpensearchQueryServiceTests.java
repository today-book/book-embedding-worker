package org.todaybook.embedding.infrastructure.opensearch.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpensearchQueryServiceTests {

  @Autowired private OpensearchQueryService embeddingService;

  @Test
  @DisplayName("Opensearch ID로 검색 테스트")
  void test1() {
    String id = "37aca826-6b57-480a-96b0-8be00ab47081";

    Optional<Document> result = embeddingService.getDocumentById(id);

    assertTrue(result.isPresent(), "Document should be found");
    Document doc = result.get();

    assertEquals(id, doc.getId());

    System.out.println(doc);
  }

  @Test
  @DisplayName("Opensearch ID 리스트로 검색 테스트")
  void test2() {
    List<String> ids = List.of("37aca826-6b57-480a-96b0-8be00ab47081");

    List<Document> result = embeddingService.getDocumentByIds(ids);

    assertEquals(ids.size(), result.size());

    System.out.println(result);
  }
}

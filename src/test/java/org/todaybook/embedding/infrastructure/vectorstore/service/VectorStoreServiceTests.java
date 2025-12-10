package org.todaybook.embedding.infrastructure.vectorstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.todaybook.embedding.config.TestPostgresContainerConfig;

@SpringBootTest
@Import({TestPostgresContainerConfig.class})
class VectorStoreServiceTests {

  @Value("${spring.ai.vectorstore.opensearch.uris}")
  private String uris;

  @Autowired private VectorStoreService vectorStoreService;

  @Test
  @DisplayName("VectorStore 저장 테스트: Opensearch에 저장")
  void test1() throws Exception {
    vectorStoreService.save(
        List.of(
            new Document(
                "11111111-1111-1111-1111-111111111111", "title: 도서 제목", Map.of("title", "도서 제목")),
            new Document(
                "22222222-2222-2222-2222-222222222222", "title: 도서 제목", Map.of("title", "도서 제목")),
            new Document(
                "33333333-3333-3333-3333-333333333333", "title: 도서 제목", Map.of("title", "도서 제목"))));

    System.out.println("opensearchUris: " + uris);
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(uris + "/book-embedding-index/_count"))
            .GET()
            .build();

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode body = mapper.readTree(response.body());

    System.out.println("body: " + body);
  }
}

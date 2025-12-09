package org.todaybook.embedding.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
import org.todaybook.embedding.infrastructure.vectorstore.service.VectorStoreService;

@SpringBootTest
@Import({
  TestPostgresContainerConfig.class,
  TestOpensearchContainerConfig.class,
  TestEmbeddingConfig.class
})
public class TestEmbeddingConfigTests {

  @Value("${spring.ai.vectorstore.opensearch.uris}")
  private String uris;

  @Autowired private VectorStoreService vectorStoreService;

  @Test
  @DisplayName("EmbeddingModel Mocking 테스트")
  void test1() throws Exception {
    String id = "11111111-1111-1111-1111-111111111111";

    vectorStoreService.save(List.of(new Document(id, "title: 도서 제목", Map.of("title", "도서 제목"))));

    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(uris + "/book-embedding-index/_doc/" + id))
            .GET()
            .build();

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode body = mapper.readTree(response.body());

    // 원본
    JsonNode embeddingNode = body.path("_source").path("embedding");
    float[] embedding = new float[embeddingNode.size()];
    for (int i = 0; i < embeddingNode.size(); i++) {
      embedding[i] = (float) embeddingNode.get(i).asDouble();
    }

    // TestEmbeddingConfig 설정값
    float[] expected = {1f, 2f, 3f};

    assertArrayEquals(expected, embedding);

    System.out.println("body: " + body);
  }
}

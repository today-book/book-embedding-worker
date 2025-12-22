package org.todaybook.embedding.infrastructure.batch.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.todaybook.embedding.config.TestEmbeddingConfig;
import org.todaybook.embedding.config.TestOpensearchContainerConfig;
import org.todaybook.embedding.config.TestPostgresContainerConfig;
import org.todaybook.embedding.infrastructure.opensearch.service.OpensearchQueryService;
import org.todaybook.embedding.infrastructure.vectorstore.service.VectorStoreService;

@Disabled
@SpringBootTest
@SpringBatchTest
@Sql({
  "/org/springframework/batch/core/schema-postgresql.sql",
  "classpath:sql/init.sql",
  "classpath:sql/book-data.sql"
})
@Import({
  TestPostgresContainerConfig.class,
  TestOpensearchContainerConfig.class,
  TestEmbeddingConfig.class
})
public class EmbeddingBatchIntegrationTests {

  @Value("${spring.ai.vectorstore.opensearch.uris}")
  private String uris;

  @Autowired private Job embeddingJob;

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired private OpensearchQueryService opensearchService;

  @Autowired private VectorStoreService vectorStoreService;

  @BeforeEach
  void setup() {
    // upsert 테스트를 위한 데이터 세팅
    vectorStoreService.save(
        List.of(
            new Document(
                "11111111-1111-1111-1111-111111111111", "title: 도서 제목", Map.of("title", "도서 제목"))));
  }

  @Test
  @DisplayName("배치를 실제로 실행하여 OpenSearch에 문서가 저장되는지 검증")
  void test1() throws Exception {
    jobLauncherTestUtils.setJob(embeddingJob);

    JobExecution execution =
        jobLauncherTestUtils.launchJob(
            new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());

    assertThat(execution.getStatus().isUnsuccessful()).isFalse();

    // 전체 문서 조회 (기본 10건)
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(uris + "/book-embedding-index/_count"))
            .GET()
            .build();

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode body = mapper.readTree(response.body());
    int count = body.get("count").asInt();

    assertEquals(10, count);

    // upsert 테스트
    Optional<Document> result =
        opensearchService.getDocumentById("11111111-1111-1111-1111-111111111111");

    assertTrue(result.isPresent());
    assertEquals("테스트책1", result.get().getMetadata().get("title"));
  }
}

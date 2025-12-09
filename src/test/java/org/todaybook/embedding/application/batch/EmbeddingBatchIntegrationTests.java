package org.todaybook.embedding.application.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.todaybook.embedding.config.TestContainersConfig;
import org.todaybook.embedding.application.service.EmbeddingService;

@SpringBootTest
@SpringBatchTest
@Sql({
  "/org/springframework/batch/core/schema-postgresql.sql",
  "classpath:sql/init.sql",
  "classpath:sql/book-data.sql"
})
@Import({TestContainersConfig.class})
public class EmbeddingBatchIntegrationTests {

  @Autowired private Job embeddingJob;

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired private EmbeddingService opensearchService;

  @Test
  @DisplayName("배치를 실제로 실행하여 OpenSearch에 문서가 저장되는지 검증")
  void test1() throws Exception {
    jobLauncherTestUtils.setJob(embeddingJob);

    JobExecution execution =
        jobLauncherTestUtils.launchJob(
            new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());

    // then
    assertThat(execution.getStatus().isUnsuccessful()).isFalse();

    // OpenSearch에서 일부 문서를 조회해본다 (예: id=1,2)
    List<Document> docs =
        opensearchService.getDocumentByIds(List.of("11111111-1111-1111-1111-111111111111"));

    assertThat(docs).isNotEmpty();
    assertThat(docs.getFirst().getFormattedContent()).contains("title:");
  }
}

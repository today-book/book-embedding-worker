package org.todaybook.embedding.config;

import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

  @Bean
  @ServiceConnection
  public PostgreSQLContainer postgres() {
    return new PostgreSQLContainer("postgres:17")
        .withDatabaseName("testdb")
        .withUsername("root")
        .withPassword("1234");
  }

  @Bean
  public OpensearchContainer opensearch() {
    OpensearchContainer container = new OpensearchContainer(DockerImageName.parse("opensearchproject/opensearch:2.0.0"));
    container.start();
    System.setProperty("spring.ai.vectorstore.opensearch.uris", container.getHttpHostAddress());
    return container;
  }
}

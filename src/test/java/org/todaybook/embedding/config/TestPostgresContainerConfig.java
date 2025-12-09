package org.todaybook.embedding.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration
public class TestPostgresContainerConfig {

  @Bean
  @ServiceConnection
  public PostgreSQLContainer postgres() {
    return new PostgreSQLContainer("postgres:17")
        .withDatabaseName("testdb")
        .withUsername("root")
        .withPassword("1234");
  }
}

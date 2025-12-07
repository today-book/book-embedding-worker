package org.todaybook.embedding.infrastructure.batch.config;

import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.BookMapper;
import org.todaybook.embedding.infrastructure.batch.service.JobExecutionService;

@Configuration
@RequiredArgsConstructor
public class EmbeddingReaderConfig {

  private static final LocalDateTime INITIAL_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);

  private final DataSource dataSource;
  private final JobExecutionService jobExecutionService;

  @Bean
  @StepScope
  public JdbcPagingItemReader<Book> reader() throws Exception {
    LocalDateTime time = jobExecutionService.getLastExecutionTime("embeddingJob");
    time = time != null ? time : INITIAL_TIME;

    Map<String, Object> params = Map.of("updated_at", time);

    return new JdbcPagingItemReaderBuilder<Book>()
        .name("bookReader")
        .dataSource(dataSource)
        .queryProvider(queryProvider())
        .parameterValues(params)
        .rowMapper(new BookMapper())
        .pageSize(100)
        .build();
  }

  @Bean
  public PagingQueryProvider queryProvider() throws Exception {
    SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();

    provider.setDataSource(dataSource);
    provider.setSelectClause("SELECT id, isbn, title, categories, description, author, publisher, published_at, created_at, updated_at");
    provider.setFromClause("from book.p_books");
    provider.setWhereClause("where updated_at > :updated_at");
    provider.setSortKeys(Map.of(
        "updated_at", Order.ASCENDING,
        "id", Order.ASCENDING
    ));

    return provider.getObject();
  }
}

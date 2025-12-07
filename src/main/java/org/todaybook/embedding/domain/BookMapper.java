package org.todaybook.embedding.domain;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class BookMapper implements RowMapper<Book> {
	@Override
	public Book mapRow(ResultSet rs, int row) throws SQLException {
    List<String> categories = Collections.emptyList();
    Array array = rs.getArray("categories");
    if (array != null) {
      String[] arr = (String[]) array.getArray();
      categories = Arrays.asList(arr);
    }

    LocalDate publishedAt = rs.getDate("published_at") != null
        ? rs.getDate("published_at").toLocalDate()
        : null;

		LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null
			? rs.getTimestamp("updated_at").toLocalDateTime()
			: null;

		return new Book(
        UUID.fromString(rs.getString("id")),
      rs.getString("isbn"),
			rs.getString("title"),
        categories,
      rs.getString("description"),
      rs.getString("author"),
      rs.getString("publisher"),
        publishedAt,
			rs.getTimestamp("created_at").toLocalDateTime(),
			updatedAt
		);
	}
}

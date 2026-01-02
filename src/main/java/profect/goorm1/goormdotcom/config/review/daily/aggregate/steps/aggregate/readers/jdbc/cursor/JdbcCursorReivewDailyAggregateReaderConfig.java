package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.readers.jdbc.cursor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.*;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorReivewDailyAggregateReaderConfig {

    @Bean
    @StepScope
    public JdbcCursorItemReader<ReviewDailyAggregate> reviewDailyAggregateReader(
            DataSource dataSource,
            @Value("#{jobParameters['statDate']}") LocalDate statDate
    ) {

        LocalDateTime fromStatDate = statDate.atStartOfDay();
        LocalDateTime toStatDate = fromStatDate.plusDays(1);

        Timestamp from = Timestamp.from(fromStatDate.toInstant(ZoneOffset.UTC));
        Timestamp to = Timestamp.from(toStatDate.toInstant(ZoneOffset.UTC));

        String sql = """
                SELECT
                  r.product_id,
                  COUNT(*)      AS review_count,
                  SUM(r.rating) AS rating_sum
                FROM p_review r
                WHERE r.deleted_at IS NULL
                  AND r.created_at >= ?
                  AND r.created_at <  ?
                GROUP BY r.product_id
                """;

        RowMapper<ReviewDailyAggregate> rowMapper = (rs, rowNum) -> new ReviewDailyAggregate(
                statDate,
                rs.getObject("product_id", java.util.UUID.class),
                rs.getLong("review_count"),
                rs.getLong("rating_sum")
        );

        JdbcCursorItemReader<ReviewDailyAggregate> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setPreparedStatementSetter(ps -> {
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
        });
        reader.setRowMapper(rowMapper);
        return reader;
    }
}
package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers.jdbc.cursor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import profect.goorm1.goormdotcom.domain.Review;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcCursorReviewItemReaderConfig {

    @Bean
    @StepScope
    public JdbcCursorItemReader<Review> reviewItemReader(
        @Qualifier(value = "productDataSource") DataSource dataSource,
        @Value("#{jobParameters['statDate']}") LocalDate statDate
    ) {

        LocalDateTime fromStatDate = statDate.atStartOfDay();
        LocalDateTime toStatDate = fromStatDate.plusDays(1);
        log.info("fromStatDate: {}", fromStatDate);
        log.info("toStatDate: {}", toStatDate);

        Timestamp from = Timestamp.from(fromStatDate.toInstant(ZoneOffset.UTC));
        Timestamp to = Timestamp.from(toStatDate.toInstant(ZoneOffset.UTC));

        String sql = """
                SELECT
                    id, order_id, product_id, user_id,
                    content, rating,
                    created_at, updated_at
                FROM p_review
                WHERE deleted_at IS NULL
                  AND created_at >= ?
                  AND created_at <  ?
                """;

        RowMapper<Review> rowMapper = (rs, rowNum) -> new Review(
                rs.getObject("id", java.util.UUID.class),
                rs.getObject("order_id", java.util.UUID.class),
                rs.getObject("product_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getString("content"),
                rs.getInt("rating"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );

        JdbcCursorItemReader<Review> reader = new JdbcCursorItemReader<>();
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

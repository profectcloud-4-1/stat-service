package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.writer.jdbc.batch;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

import javax.sql.DataSource;

@Configuration
public class JdbcBatchReviewDailyAggregateWriterConfig {

    @Bean
    public JdbcBatchItemWriter<ReviewDailyAggregate> reviewDailyAggregateWriter(
            DataSource dataSource
    ) {
        String sql = """
                INSERT INTO p_review_daily (stat_date, product_id, review_count, rating_sum, updated_at)
                VALUES (:statDate, :productId, :reviewCount, :ratingSum, CURRENT_TIMESTAMP)
                ON CONFLICT (stat_date, product_id)
                DO UPDATE SET
                  review_count = EXCLUDED.review_count,
                  rating_sum   = EXCLUDED.rating_sum,
                  updated_at   = CURRENT_TIMESTAMP
                """;

        return new JdbcBatchItemWriterBuilder<ReviewDailyAggregate>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}

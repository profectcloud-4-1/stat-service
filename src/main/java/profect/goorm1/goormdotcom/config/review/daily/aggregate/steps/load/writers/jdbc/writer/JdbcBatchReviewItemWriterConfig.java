package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.writers.jdbc.writer;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import profect.goorm1.goormdotcom.domain.Review;

import javax.sql.DataSource;

@Configuration
public class JdbcBatchReviewItemWriterConfig {

    @Bean
    public JdbcBatchItemWriter<Review> reviewItemWriter(
            DataSource dataSource
    ) {
        String sql = """
                INSERT INTO p_review (id, user_id, product_id, order_id, rating, content, created_at, updated_at)
                VALUES (:id, :userId, :productId, :orderId, :rating, :content, :createdAt, :updatedAt)
                ON CONFLICT (id)
                DO UPDATE SET
                    user_id    = EXCLUDED.user_id,
                    product_id = EXCLUDED.product_id,
                    order_id   = EXCLUDED.order_id,
                    rating     = EXCLUDED.rating,
                    content    = EXCLUDED.content,
                    created_at = EXCLUDED.created_at,
                    updated_at = EXCLUDED.updated_at
                """;
        return new JdbcBatchItemWriterBuilder<Review>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}

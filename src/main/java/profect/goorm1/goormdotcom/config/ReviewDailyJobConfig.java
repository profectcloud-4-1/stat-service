package profect.goorm1.goormdotcom.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import profect.goorm1.goormdotcom.components.listeners.BatchChunkListener;
import profect.goorm1.goormdotcom.components.listeners.BatchItemReadListener;
import profect.goorm1.goormdotcom.components.listeners.BatchJobListener;
import profect.goorm1.goormdotcom.components.listeners.BatchStepListener;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReviewDailyJobConfig {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int CHUNK_SIZE = 2;
    private final BatchJobListener batchJobListener;
    private final BatchStepListener batchStepListener;
    private final BatchChunkListener batchChunkListener;
    private final BatchItemReadListener<ReviewDailyAggregate> batchItemReadListener;

    @Bean
    public Job reviewDailyJob(JobRepository jobRepository, Step reviewDailyStep) {
        return new JobBuilder("reviewDailyJob", jobRepository)
                .listener(batchJobListener)
                .start(reviewDailyStep)
                .build();
    }

    @Bean
    public Step reviewDailyStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcCursorItemReader<ReviewDailyAggregate> reviewDailyReader,
            ItemWriter<ReviewDailyAggregate> reviewDailyWriter
    ) {
        return new StepBuilder("reviewDailyStep", jobRepository)
                .listener(batchStepListener)
                .<ReviewDailyAggregate, ReviewDailyAggregate>chunk(CHUNK_SIZE, transactionManager)
                .listener(batchChunkListener)
                .listener(batchItemReadListener)
                .reader(reviewDailyReader)
                .writer(reviewDailyWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<ReviewDailyAggregate> reviewDailyReader(
            DataSource dataSource,
            @Value("#{jobParameters['statDate']}") String statDate
    ) {
        LocalDate date = LocalDate.parse(statDate);

        // KST 기준 하루 [from, to)
        ZonedDateTime fromZdt = date.atStartOfDay(KST);
        ZonedDateTime toZdt = date.plusDays(1).atStartOfDay(KST);

        Timestamp from = Timestamp.from(fromZdt.toInstant());
        Timestamp to = Timestamp.from(toZdt.toInstant());

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
                date,
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

    @Bean
    public JdbcBatchItemWriter<ReviewDailyAggregate> reviewDailyWriter(DataSource dataSource) {
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

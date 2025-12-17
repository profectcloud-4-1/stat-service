package profect.goorm1.goormdotcom.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
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
import java.time.format.DateTimeParseException;

@Configuration
public class ReviewDailyJobConfig {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int CHUNK_SIZE = 500;

    @Bean
    public Job reviewDailyJob(JobRepository jobRepository, Step reviewDailyStep) {
        return new JobBuilder("reviewDailyJob", jobRepository)
                .validator(statDateValidator())
                .incrementer(new RunIdIncrementer())
                .start(reviewDailyStep)
                .build();
    }

    @Bean
    public Job nonRedundantReviewDailyJob(JobRepository jobRepository, Step reviewDailyStep) {
        return new JobBuilder("nonRedundantReviewDailyJob", jobRepository)
                .validator(statDateValidator())
                .start(reviewDailyStep)
                .build();
    }

    @Bean
    public Job noRestartReviewDailyJob(JobRepository jobRepository, Step reviewDailyStep) {
        return new JobBuilder("noRestartReviewDailyJob", jobRepository)
                .preventRestart()
                .validator(statDateValidator())
                .start(reviewDailyStep)
                .build();
    }

    @Bean
    public JobParametersValidator statDateValidator() {
        return new JobParametersValidator() {
            @Override
            public void validate(JobParameters parameters) throws JobParametersInvalidException {
                String statDate = parameters.getString("statDate");
                if (statDate == null || statDate.isBlank()) {
                    throw new JobParametersInvalidException("Missing required JobParameter: statDate (yyyy-MM-dd)");
                }
                try {
                    LocalDate.parse(statDate); // ISO yyyy-MM-dd
                } catch (DateTimeParseException e) {
                    throw new JobParametersInvalidException("Invalid statDate format. Expected yyyy-MM-dd, got: " + statDate);
                }
            }
        };
    }

    @Bean
    public Step reviewDailyStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcCursorItemReader<ReviewDailyAggregate> reviewDailyReader,
            ItemWriter<ReviewDailyAggregate> reviewDailyWriter
    ) {
        return new StepBuilder("reviewDailyStep", jobRepository)
                .<ReviewDailyAggregate, ReviewDailyAggregate>chunk(CHUNK_SIZE, transactionManager)
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
        String upsert = """
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
                .sql(upsert)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    public static class ReviewDailyAggregate {
        private final LocalDate statDate;
        private final java.util.UUID productId;
        private final long reviewCount;
        private final long ratingSum;

        public ReviewDailyAggregate(LocalDate statDate, java.util.UUID productId, long reviewCount, long ratingSum) {
            this.statDate = statDate;
            this.productId = productId;
            this.reviewCount = reviewCount;
            this.ratingSum = ratingSum;
        }

        public LocalDate getStatDate() { return statDate; }
        public java.util.UUID getProductId() { return productId; }
        public long getReviewCount() { return reviewCount; }
        public long getRatingSum() { return ratingSum; }
    }
}

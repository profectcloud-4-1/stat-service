package profect.goorm1.goormdotcom.config.review.daily.aggregate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import profect.goorm1.goormdotcom.common.listeners.BatchJobListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReviewDailyAggregateJobConfig {

    private final Step reviewDailyLoadStep;
    private final Step reviewDailyAggregateStep;
    private final Step exampleReviewDailyAggregateStep;
    private final BatchJobListener batchJobListener;

    @Bean
    public Job reviewDailyAggregateJob(JobRepository jobRepository) {
        return new JobBuilder("reviewDailyAggregateJob", jobRepository)
                .listener(batchJobListener)
                .start(reviewDailyLoadStep)
                .next(reviewDailyAggregateStep)
                .build();
    }

    @Bean
    public Job exampleReviewDailyAggregateJob(JobRepository jobRepository) {
        return new JobBuilder("exampleReviewDailyAggregateJob", jobRepository)
                .listener(batchJobListener)
                .start(reviewDailyLoadStep)
                .next(exampleReviewDailyAggregateStep)
                .build();
    }
}

package profect.goorm1.goormdotcom.config.review.daily.aggregate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import profect.goorm1.goormdotcom.common.listeners.BatchJobListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class noLoadReviewDailyAggregateJobConfig {

    private final Step reviewDailyAggregateStep;
    private final BatchJobListener batchJobListener;

    @Bean
    public Job noLoadReviewDailyAggregateJob(JobRepository jobRepository) {
        return new JobBuilder("noLoadReviewDailyAggregateJob", jobRepository)
                .listener(batchJobListener)
                .start(reviewDailyAggregateStep)
                .build();
    }
}

package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import profect.goorm1.goormdotcom.common.listeners.BatchChunkListener;
import profect.goorm1.goormdotcom.common.listeners.BatchStepListener;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers.ReviewItemReader;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.writers.ReviewItemWriter;
import profect.goorm1.goormdotcom.domain.Review;

@Configuration
@RequiredArgsConstructor
public class ReviewDailyLoadStepConfig {

    private static final int CHUNK_SIZE = 2;

    private final ReviewItemReader reader;
    private final ReviewItemWriter writer;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BatchStepListener batchStepListener;
    private final BatchChunkListener batchChunkListener;

    @Bean
    public Step reviewDailyLoadStep() {
        return new StepBuilder("reviewDailyLoadStep", jobRepository)
                .listener(batchStepListener)
                .<Review, Review>chunk(CHUNK_SIZE, transactionManager)
                .listener(batchChunkListener)
                .reader(reader)
                .writer(writer)
                .build();
    }
}

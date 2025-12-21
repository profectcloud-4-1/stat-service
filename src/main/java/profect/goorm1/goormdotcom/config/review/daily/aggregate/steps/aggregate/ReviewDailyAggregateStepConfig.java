package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import profect.goorm1.goormdotcom.common.listeners.BatchChunkListener;
import profect.goorm1.goormdotcom.common.listeners.BatchItemReadListener;
import profect.goorm1.goormdotcom.common.listeners.BatchStepListener;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.reader.ReivewDailyAggregateReader;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.writer.ReviewDailyAggregateWriter;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

@Configuration
@RequiredArgsConstructor
public class ReviewDailyAggregateStepConfig {
    private static final int CHUNK_SIZE = 2;

    private final ReivewDailyAggregateReader reader;
    private final ReviewDailyAggregateWriter writer;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BatchStepListener batchStepListener;
    private final BatchChunkListener batchChunkListener;
    private final BatchItemReadListener<ReviewDailyAggregate> batchItemReadListener;

    @Bean
    public Step reviewDailyAggregateStep() {
        return new StepBuilder("reviewDailyAggregateStep", jobRepository)
                .listener(batchStepListener)
                .<ReviewDailyAggregate, ReviewDailyAggregate>chunk(CHUNK_SIZE, transactionManager)
                .listener(batchChunkListener)
                .listener(batchItemReadListener)
                .reader(reader)
                .writer(writer)
                .build();
    }
}

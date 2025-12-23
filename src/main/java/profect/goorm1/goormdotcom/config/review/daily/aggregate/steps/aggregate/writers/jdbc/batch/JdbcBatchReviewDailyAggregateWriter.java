package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.writers.jdbc.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.writers.ReviewDailyAggregateWriter;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

@Component
@RequiredArgsConstructor
public class JdbcBatchReviewDailyAggregateWriter implements ReviewDailyAggregateWriter {

    private final JdbcBatchItemWriter<ReviewDailyAggregate> reviewDailyAggregateWriter;

    @Override
    public void write(Chunk<? extends ReviewDailyAggregate> chunk) throws Exception {
        reviewDailyAggregateWriter.write(chunk);
    }

}

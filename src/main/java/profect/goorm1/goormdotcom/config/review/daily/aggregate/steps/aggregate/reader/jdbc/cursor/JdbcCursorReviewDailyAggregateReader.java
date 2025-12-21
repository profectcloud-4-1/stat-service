package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.reader.jdbc.cursor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.stereotype.Component;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.reader.ReivewDailyAggregateReader;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

@Component
@RequiredArgsConstructor
public class JdbcCursorReviewDailyAggregateReader implements ReivewDailyAggregateReader {

    private final JdbcCursorItemReader<ReviewDailyAggregate> reviewDailyAggregateReader;

    @Override
    public ReviewDailyAggregate read() throws Exception {
        return reviewDailyAggregateReader.read();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        reviewDailyAggregateReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        reviewDailyAggregateReader.update(executionContext);
    }

    @Override
    public void close() {
        reviewDailyAggregateReader.close();
    }
}

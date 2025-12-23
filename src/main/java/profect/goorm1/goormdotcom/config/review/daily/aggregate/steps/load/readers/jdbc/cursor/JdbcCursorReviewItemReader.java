package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers.jdbc.cursor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Configuration;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers.ReviewItemReader;
import profect.goorm1.goormdotcom.domain.Review;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorReviewItemReader implements ReviewItemReader {

    private final JdbcCursorItemReader<Review> reviewItemReader;

    @Override
    public Review read() throws Exception {
        return reviewItemReader.read();
    }

    @Override
    public void open(ExecutionContext excutionContext) {
        reviewItemReader.open(excutionContext);
    }

    @Override
    public void update(ExecutionContext excutionContext) {
        reviewItemReader.update(excutionContext);
    }

    @Override
    public void close() {
        reviewItemReader.close();
    }
}

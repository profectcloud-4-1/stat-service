package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.writers.jdbc.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;
import profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.writers.ReviewItemWriter;
import profect.goorm1.goormdotcom.domain.Review;

@Component
@RequiredArgsConstructor
public class JdbcBatchReviewItemWriter implements ReviewItemWriter {

    private final JdbcBatchItemWriter<Review> reviewItemWriter;

    @Override
    public void write(Chunk<? extends Review> chunk) throws Exception {
        reviewItemWriter.write(chunk);
    }
}

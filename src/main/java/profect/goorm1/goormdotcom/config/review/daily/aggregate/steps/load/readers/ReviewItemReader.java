package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.readers;

import org.springframework.batch.item.ItemStreamReader;
import profect.goorm1.goormdotcom.domain.Review;

public interface ReviewItemReader extends ItemStreamReader<Review> {
}

package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.load.writers;

import org.springframework.batch.item.ItemStreamWriter;
import profect.goorm1.goormdotcom.domain.Review;

public interface ReviewItemWriter extends ItemStreamWriter<Review> {
}

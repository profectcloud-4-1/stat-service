package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.writers;

import org.springframework.batch.item.ItemStreamWriter;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

public interface ReviewDailyAggregateWriter extends ItemStreamWriter<ReviewDailyAggregate> {
}

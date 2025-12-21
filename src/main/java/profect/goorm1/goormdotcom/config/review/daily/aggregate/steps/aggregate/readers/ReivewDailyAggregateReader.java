package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.readers;

import org.springframework.batch.item.ItemStreamReader;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

public interface ReivewDailyAggregateReader extends ItemStreamReader<ReviewDailyAggregate> {
}

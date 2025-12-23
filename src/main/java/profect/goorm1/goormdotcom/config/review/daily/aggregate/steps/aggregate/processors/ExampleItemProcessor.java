package profect.goorm1.goormdotcom.config.review.daily.aggregate.steps.aggregate.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import profect.goorm1.goormdotcom.domain.ReviewDailyAggregate;

@Component
@StepScope
@RequiredArgsConstructor
public class ExampleItemProcessor implements ItemProcessor<ReviewDailyAggregate, ReviewDailyAggregate> {

    @Value("#{jobParameters['customParam']}")
    private String customParam;

    @Override
    public ReviewDailyAggregate process(ReviewDailyAggregate item) throws Exception {
        if (customParam.equals("example-exception")) {
            throw new Exception("[Example] exception");
        }
        return item;
    }
}

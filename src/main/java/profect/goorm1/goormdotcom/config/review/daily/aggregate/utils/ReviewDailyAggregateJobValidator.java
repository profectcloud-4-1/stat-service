package profect.goorm1.goormdotcom.config.review.daily.aggregate.utils;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class ReviewDailyAggregateJobValidator implements JobParametersValidator {

    @Override
    public void validate(@Nullable JobParameters jobParameters) throws JobParametersInvalidException {

        LocalDate statDate = jobParameters.getLocalDate("statDate");
        if (statDate == null) {
            throw new JobParametersInvalidException("필수 파라미터인 statDate 파라미터를 포함하고 있지 않습니다.");
        }

    }
}

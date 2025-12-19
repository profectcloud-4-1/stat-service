package profect.goorm1.goormdotcom.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDailyAggregate {

    private LocalDate statDate;
    private UUID productId;
    private long reviewCount;
    private long ratingSum;
}

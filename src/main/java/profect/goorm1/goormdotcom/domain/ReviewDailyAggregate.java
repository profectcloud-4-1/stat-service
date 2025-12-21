package profect.goorm1.goormdotcom.domain;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewDailyAggregate (
    LocalDate statDate,
    UUID productId,
    long reviewCount,
    long ratingSum
) {
}

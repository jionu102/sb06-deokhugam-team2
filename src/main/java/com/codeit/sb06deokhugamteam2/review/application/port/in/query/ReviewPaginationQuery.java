package com.codeit.sb06deokhugamteam2.review.application.port.in.query;

import java.time.Instant;
import java.util.UUID;

public record ReviewPaginationQuery(
        UUID userId,
        UUID bookId,
        String keyword,
        String orderBy,
        String direction,
        String cursor,
        Instant after,
        Integer limit,
        UUID requestUserId
) {
}

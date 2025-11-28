package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;
import java.util.function.Function;

public interface ReviewRepository {

    void existsByBookIdAndUserId(UUID bookId, UUID userId, Function<UUID, ? extends ReviewException> exceptionFunction);

    ReviewDetail save(ReviewDomain review);
}

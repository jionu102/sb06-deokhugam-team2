package com.codeit.sb06deokhugamteam2.review.domain.port;

import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

    void save(ReviewDomain review);

    Optional<ReviewDomain> findById(UUID reviewId);

    void delete(ReviewDomain review);

    Optional<ReviewDomain> findByIdWithoutDeleted(UUID reviewId);

    void hardDelete(ReviewDomain review);

    void update(ReviewDomain review);
}

package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewDomain;

import java.util.Optional;
import java.util.UUID;

public interface SaveReviewPort {

    void save(ReviewDomain.Snapshot reviewSnapshot);

    void softDelete(UUID reviewId);

    void hardDelete(UUID reviewId);

    Optional<ReviewDomain.Snapshot> findById(UUID reviewId);

    Optional<ReviewDomain.Snapshot> findByIdWithoutDeleted(UUID reviewId);

    void update(ReviewDomain.Snapshot reviewSnapshot);

    Optional<ReviewDomain.Snapshot> findByIdForUpdate(UUID reviewId);
}

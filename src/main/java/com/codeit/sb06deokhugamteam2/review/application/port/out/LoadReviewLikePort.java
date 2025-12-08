package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewLikeDomain;

import java.util.Optional;
import java.util.UUID;

public interface LoadReviewLikePort {

    Optional<ReviewLikeDomain> findById(UUID reviewId, UUID userId);
}

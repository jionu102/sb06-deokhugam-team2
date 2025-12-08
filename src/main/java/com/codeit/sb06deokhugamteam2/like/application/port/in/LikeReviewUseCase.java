package com.codeit.sb06deokhugamteam2.like.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface LikeReviewUseCase {

    void likeReview(UUID reviewId, UUID userId, Instant likedAt);
}

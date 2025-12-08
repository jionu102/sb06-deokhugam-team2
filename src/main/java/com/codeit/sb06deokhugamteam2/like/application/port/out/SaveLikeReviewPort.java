package com.codeit.sb06deokhugamteam2.like.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface SaveLikeReviewPort {

    void delete(UUID reviewId);

    void save(UUID reviewId, UUID userId, Instant likedAt);

    void delete(UUID reviewId, UUID userId);
}

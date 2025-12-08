package com.codeit.sb06deokhugamteam2.review.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ReviewLikedEvent(UUID reviewId, UUID userId, Instant likedAt) {
}

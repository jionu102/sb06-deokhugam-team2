package com.codeit.sb06deokhugamteam2.review.domain.model;

import java.util.UUID;

public class ReviewLikeDomain {

    private final UUID reviewId;
    private final UUID userId;
    private boolean isLike;

    public ReviewLikeDomain(UUID reviewId, UUID userId, boolean isLike) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.isLike = isLike;
    }

    public ReviewLikeDomain toggleLike() {
        isLike = !isLike;
        return this;
    }

    public UUID reviewId() {
        return reviewId;
    }

    public UUID userId() {
        return userId;
    }

    public boolean isLiked() {
        return isLike;
    }
}

package com.codeit.sb06deokhugamteam2.like.adapter.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ReviewLikeId implements Serializable {

    @NotNull
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewId;

    public ReviewLikeId userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public ReviewLikeId reviewId(UUID reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    public UUID userId() {
        return userId;
    }

    public UUID reviewId() {
        return reviewId;
    }
}

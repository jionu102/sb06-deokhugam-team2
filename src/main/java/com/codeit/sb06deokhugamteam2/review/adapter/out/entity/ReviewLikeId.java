package com.codeit.sb06deokhugamteam2.review.adapter.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ReviewLikeId implements Serializable {

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReviewLikeId entity = (ReviewLikeId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.reviewId, entity.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, reviewId);
    }

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

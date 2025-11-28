package com.codeit.sb06deokhugamteam2.review.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public record ReviewLikeId(
        @NotNull @Column(name = "user_id", nullable = false)
        UUID userId,
        @NotNull @Column(name = "review_id", nullable = false)
        UUID reviewId
) implements Serializable {

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
}

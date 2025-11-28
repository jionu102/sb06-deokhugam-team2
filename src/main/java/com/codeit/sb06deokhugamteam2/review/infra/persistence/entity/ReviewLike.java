package com.codeit.sb06deokhugamteam2.review.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "ReviewLikes")
public class ReviewLike {

    @EmbeddedId
    private ReviewLikeId id;

    @NotNull
    @Column(name = "liked_at", nullable = false)
    private Instant likedAt;

    public ReviewLike id(ReviewLikeId id) {
        this.id = id;
        return this;
    }

    public ReviewLike likedAt(Instant likedAt) {
        this.likedAt = likedAt;
        return this;
    }

    public ReviewLikeId getId() {
        return id;
    }

    public Instant getLikedAt() {
        return likedAt;
    }
}

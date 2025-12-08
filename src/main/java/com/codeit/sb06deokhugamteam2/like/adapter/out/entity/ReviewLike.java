package com.codeit.sb06deokhugamteam2.like.adapter.out.entity;

import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "review_likes")
public class ReviewLike {

    @EmbeddedId
    private ReviewLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", updatable = false, nullable = false)
    private Review review;

    @NotNull
    @Column(name = "liked_at", updatable = false, nullable = false)
    private Instant likedAt;

    public ReviewLike id(ReviewLikeId id) {
        this.id = id;
        return this;
    }

    public ReviewLike user(User user) {
        this.user = user;
        return this;
    }

    public ReviewLike review(Review review) {
        this.review = review;
        return this;
    }

    public ReviewLike likedAt(Instant likedAt) {
        this.likedAt = likedAt;
        return this;
    }

    public ReviewLikeId id() {
        return id;
    }

    public User user() {
        return user;
    }

    public Review review() {
        return review;
    }

    public Instant likedAt() {
        return likedAt;
    }
}

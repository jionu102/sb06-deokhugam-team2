package com.codeit.sb06deokhugamteam2.review.adapter.out.entity;

import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "reviewlikes")
public class ReviewLike {

    @EmbeddedId
    private ReviewLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @NotNull
    @Column(name = "liked_at", nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReviewLike that = (ReviewLike) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

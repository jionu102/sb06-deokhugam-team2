package com.codeit.sb06deokhugamteam2.review.domain.model;

import java.util.UUID;

public class ReviewStatDomain {

    private final UUID reviewId;
    private ReviewCountDomain likeCount;
    private ReviewCountDomain commentCount;

    public ReviewStatDomain(UUID reviewId, ReviewCountDomain likeCount, ReviewCountDomain commentCount) {
        this.reviewId = reviewId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public static ReviewStatDomain create(UUID reviewId) {
        ReviewCountDomain likeCount = ReviewCountDomain.ZERO;
        ReviewCountDomain commentCount = ReviewCountDomain.ZERO;
        return new ReviewStatDomain(reviewId, likeCount, commentCount);
    }

    public static ReviewStatDomain from(Snapshot reviewStatSnapshot) {
        UUID reviewId = reviewStatSnapshot.reviewId();
        ReviewCountDomain likeCount = reviewStatSnapshot.likeCount();
        ReviewCountDomain commentCount = reviewStatSnapshot.commentCount();
        return new ReviewStatDomain(reviewId, likeCount, commentCount);
    }

    public Snapshot toSnapshot() {
        return new Snapshot(reviewId, likeCount, commentCount);
    }

    public ReviewStatDomain increaseLikeCount() {
        likeCount = likeCount.increment();
        return this;
    }

    public ReviewStatDomain decreaseLikeCount() {
        likeCount = likeCount.decrement();
        return this;
    }

    public record Snapshot(UUID reviewId, ReviewCountDomain likeCount, ReviewCountDomain commentCount) {
    }
}

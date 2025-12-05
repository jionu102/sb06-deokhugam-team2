package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;

public class ReviewStatDomain {

    private final UUID reviewId;
    private ReviewCount likeCount;
    private ReviewCount commentCount;

    public ReviewStatDomain(UUID reviewId, ReviewCount likeCount, ReviewCount commentCount) {
        this.reviewId = reviewId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public static ReviewStatDomain create(UUID reviewId) {
        ReviewCount likeCount = new ReviewCount(0);
        ReviewCount commentCount = new ReviewCount(0);
        return new ReviewStatDomain(reviewId, likeCount, commentCount);
    }

    public static ReviewStatDomain from(Snapshot reviewStatSnapshot) {
        UUID reviewId = reviewStatSnapshot.reviewId();
        ReviewCount likeCount = reviewStatSnapshot.likeCount();
        ReviewCount commentCount = reviewStatSnapshot.commentCount();
        return new ReviewStatDomain(reviewId, likeCount, commentCount);
    }

    public Snapshot toSnapshot() {
        return new Snapshot(reviewId, likeCount, commentCount);
    }

    public record Snapshot(UUID reviewId, ReviewCount likeCount, ReviewCount commentCount) {
    }
}

package com.codeit.sb06deokhugamteam2.review.domain.model;

import java.util.UUID;

public class ReviewBookDomain {

    private final UUID id;
    private int reviewCount;
    private int ratingSum;

    public ReviewBookDomain(UUID id, int reviewCount, int ratingSum) {
        this.id = id;
        this.reviewCount = reviewCount;
        this.ratingSum = ratingSum;
    }

    public ReviewBookDomain increaseReviewCount() {
        reviewCount++;
        return this;
    }

    public ReviewBookDomain decreaseReviewCount() {
        reviewCount--;
        return this;
    }

    public ReviewBookDomain plusReviewRating(ReviewRatingDomain rating) {
        ratingSum += rating.value();
        return this;
    }

    public ReviewBookDomain minusReviewRating(ReviewRatingDomain rating) {
        ratingSum -= rating.value();
        return this;
    }

    public UUID id() {
        return id;
    }

    public int reviewCount() {
        return reviewCount;
    }

    public int ratingSum() {
        return ratingSum;
    }
}

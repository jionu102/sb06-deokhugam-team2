package com.codeit.sb06deokhugamteam2.review.domain;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewRatingException;

public record ReviewRating(int value) {

    public ReviewRating {
        if (value < 1 || value > 5) {
            throw new InvalidReviewRatingException(value);
        }
    }
}

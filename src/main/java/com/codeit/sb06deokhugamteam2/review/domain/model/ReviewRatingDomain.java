package com.codeit.sb06deokhugamteam2.review.domain.model;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewRatingException;

public record ReviewRatingDomain(int value) {

    public ReviewRatingDomain {
        if (value < 1 || value > 5) {
            throw new InvalidReviewRatingException(value);
        }
    }

    public ReviewRatingDomain(Integer value) {
        this(value.intValue());
    }
}

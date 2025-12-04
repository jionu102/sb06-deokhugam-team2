package com.codeit.sb06deokhugamteam2.review.domain;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewCountException;

public record ReviewCount(int value) {

    public ReviewCount {
        if (value < 0) {
            throw new InvalidReviewCountException(value);
        }
    }

    public ReviewCount(Integer value) {
        this(value.intValue());
    }
}

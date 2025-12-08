package com.codeit.sb06deokhugamteam2.review.domain.model;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewCountException;

public record ReviewCountDomain(int value) {

    public static final ReviewCountDomain ZERO = new ReviewCountDomain(0);

    public ReviewCountDomain {
        if (value < 0) {
            throw new InvalidReviewCountException(value);
        }
    }

    public ReviewCountDomain(Integer value) {
        this(value.intValue());
    }

    public ReviewCountDomain increment() {
        return new ReviewCountDomain(Math.addExact(value, 1));
    }

    public ReviewCountDomain decrement() {
        return new ReviewCountDomain(Math.subtractExact(value, 1));
    }
}

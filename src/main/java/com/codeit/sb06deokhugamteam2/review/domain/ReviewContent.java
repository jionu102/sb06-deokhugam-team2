package com.codeit.sb06deokhugamteam2.review.domain;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewContentException;

public record ReviewContent(String value) {

    public ReviewContent {
        value = value.trim();
        if (value.isBlank()) {
            throw new InvalidReviewContentException();
        }
    }
}

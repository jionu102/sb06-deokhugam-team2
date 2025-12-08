package com.codeit.sb06deokhugamteam2.review.domain.model;

import com.codeit.sb06deokhugamteam2.review.domain.exception.InvalidReviewContentException;

public record ReviewContentDomain(String value) {

    public ReviewContentDomain {
        value = value.trim();
        if (value.isBlank()) {
            throw new InvalidReviewContentException();
        }
    }
}

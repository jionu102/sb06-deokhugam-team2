package com.codeit.sb06deokhugamteam2.review.domain.exception;

public class InvalidReviewCountException extends ReviewException {

    public InvalidReviewCountException(int value) {
        super("Invalid review count: '%d'".formatted(value));
    }
}

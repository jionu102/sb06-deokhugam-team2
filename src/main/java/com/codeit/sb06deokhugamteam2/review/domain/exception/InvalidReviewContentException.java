package com.codeit.sb06deokhugamteam2.review.domain.exception;

public class InvalidReviewContentException extends ReviewException {

    public InvalidReviewContentException() {
        super("Review content cannot be blank");
    }
}

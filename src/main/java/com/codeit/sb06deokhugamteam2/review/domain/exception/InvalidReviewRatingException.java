package com.codeit.sb06deokhugamteam2.review.domain.exception;

public class InvalidReviewRatingException extends ReviewException {
    
    public InvalidReviewRatingException(int ratingValue) {
        super("Invalid review rating: '%d'. Rating must be between 1 and 5".formatted(ratingValue));
    }
}

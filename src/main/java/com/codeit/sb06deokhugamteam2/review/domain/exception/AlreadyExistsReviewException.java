package com.codeit.sb06deokhugamteam2.review.domain.exception;

import java.util.UUID;

public class AlreadyExistsReviewException extends ReviewException {

    public AlreadyExistsReviewException(UUID bookId) {
        super("Review already exists for book: '%s'".formatted(bookId));
    }
}

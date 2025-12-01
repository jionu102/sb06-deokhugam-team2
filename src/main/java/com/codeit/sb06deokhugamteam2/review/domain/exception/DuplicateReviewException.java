package com.codeit.sb06deokhugamteam2.review.domain.exception;

import java.util.UUID;

public class DuplicateReviewException extends ReviewException {

    public DuplicateReviewException(UUID bookId) {
        super("Duplicate review: '%s'".formatted(bookId));
    }
}

package com.codeit.sb06deokhugamteam2.review.domain.exception;

import java.util.UUID;

public class ReviewBookNotFoundException extends ReviewException {

    public ReviewBookNotFoundException(UUID bookId) {
        super("Book not found: '%s'".formatted(bookId));
    }
}

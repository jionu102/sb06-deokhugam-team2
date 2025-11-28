package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;

public abstract sealed class ReviewException extends RuntimeException {

    private ReviewException(String message) {
        super(message);
    }

    public static final class BookNotFoundException extends ReviewException {

        public BookNotFoundException(UUID bookId) {
            super("Book not found: '%s'".formatted(bookId));
        }
    }

    public static final class UserNotFoundException extends ReviewException {

        public UserNotFoundException(UUID userId) {
            super("User not found: '%s'".formatted(userId));
        }
    }

    public static final class DuplicateReviewException extends ReviewException {

        public DuplicateReviewException(UUID bookId) {
            super("Duplicate review: '%s'".formatted(bookId));
        }
    }
}

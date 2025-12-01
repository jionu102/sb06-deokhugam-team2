package com.codeit.sb06deokhugamteam2.review.domain.exception;

import java.util.UUID;

public class ReviewUserNotFoundException extends ReviewException {

    public ReviewUserNotFoundException(UUID userId) {
        super("User not found: '%s'".formatted(userId));
    }
}

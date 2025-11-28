package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.UUID;

public record ReviewCreationCommand(
        UUID bookId,
        UUID userId,
        Integer rating,
        String content
) {
}

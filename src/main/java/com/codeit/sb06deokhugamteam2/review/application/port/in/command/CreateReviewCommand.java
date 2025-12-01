package com.codeit.sb06deokhugamteam2.review.application.port.in.command;

import java.util.UUID;

public record CreateReviewCommand(UUID bookId, UUID userId, Integer rating, String content) {
}

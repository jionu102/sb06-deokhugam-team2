package com.codeit.sb06deokhugamteam2.review.application.dto.response;

import java.util.UUID;

public record ReviewLikeDto(UUID reviewId, UUID userId, Boolean liked) {
}

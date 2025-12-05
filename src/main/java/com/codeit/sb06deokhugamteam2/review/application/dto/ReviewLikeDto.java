package com.codeit.sb06deokhugamteam2.review.application.dto;

import java.util.UUID;

public record ReviewLikeDto(UUID reviewId, UUID userId, Boolean liked) {
}

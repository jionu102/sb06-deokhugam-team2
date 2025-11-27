package com.codeit.sb06deokhugamteam2.user.dto;

import java.util.UUID;


public record PowerUserDto(
        UUID id,
        String nickname,
        long totalReviews,
        long totalLikes
) {
}

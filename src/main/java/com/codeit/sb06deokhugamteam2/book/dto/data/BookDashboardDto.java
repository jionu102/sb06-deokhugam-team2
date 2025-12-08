package com.codeit.sb06deokhugamteam2.book.dto.data;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BookDashboardDto(
        UUID id,
        Instant createdAt,
        long periodReviewCount,
        double periodRating,
        double periodScore
) {
}

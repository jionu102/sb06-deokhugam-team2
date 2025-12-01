package com.codeit.sb06deokhugamteam2.book.dto.data;

import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PopularBookDto(
        UUID id,
        UUID bookId,
        String title,
        String author,
        String thumbnailUrl,
        PeriodType period,
        long rank,
        double score,
        long reviewCount,
        double rating,
        Instant createdAt
) {
}

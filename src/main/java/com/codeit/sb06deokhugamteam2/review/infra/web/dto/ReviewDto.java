package com.codeit.sb06deokhugamteam2.review.infra.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
        UUID id,
        UUID bookId,
        String bookTitle,
        String bookThumbnailUrl,
        UUID userId,
        String userNickname,
        String content,
        Integer rating,
        Integer likeCount,
        Integer commentCount,
        Boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

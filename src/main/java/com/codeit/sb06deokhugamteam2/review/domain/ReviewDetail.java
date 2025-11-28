package com.codeit.sb06deokhugamteam2.review.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDetail(
        UUID id,
        UUID bookId,
        String bookTitle,
        String bookThumbnailUrl,
        UUID userId,
        String userNickname,
        String content,
        int rating,
        int likeCount,
        int commentCount,
        boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

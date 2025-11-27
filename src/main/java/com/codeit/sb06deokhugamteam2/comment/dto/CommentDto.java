package com.codeit.sb06deokhugamteam2.comment.dto;

import java.time.Instant;

public record CommentDto(
        String id,
        String userId,
        String reviewId,
        String userNickname,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}

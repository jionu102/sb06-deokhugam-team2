package com.codeit.sb06deokhugamteam2.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull String userId,
        @NotNull String reviewId,
        @NotBlank String content
) {
}

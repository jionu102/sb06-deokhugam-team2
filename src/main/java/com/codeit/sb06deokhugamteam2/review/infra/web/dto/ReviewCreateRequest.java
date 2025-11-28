package com.codeit.sb06deokhugamteam2.review.infra.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewCreateRequest(
        @NotNull UUID bookId,
        @NotNull UUID userId,
        @NotNull String content,
        @NotNull @Min(1) @Max(5) Integer rating
) {
}

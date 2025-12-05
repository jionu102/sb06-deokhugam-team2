package com.codeit.sb06deokhugamteam2.review.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "커서 기반 페이지 응답")
public record CursorPageResponseReviewDto(
        @Schema(description = "페이지 내용")
        List<ReviewDto> content,

        String nextCursor,

        String nextAfter,

        Integer size,

        Long totalElements,

        Boolean hasNext
) {
}

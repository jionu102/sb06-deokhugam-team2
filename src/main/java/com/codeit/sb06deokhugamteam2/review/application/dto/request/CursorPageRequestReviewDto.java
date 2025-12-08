package com.codeit.sb06deokhugamteam2.review.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.UUID;

import java.time.Instant;

public record CursorPageRequestReviewDto(
        @Schema(format = "uuid", description = "작성자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @UUID(message = "사용자 ID는 UUID 형식이어야 합니다.")
        String userId,

        @Schema(format = "uuid", description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @UUID(message = "도서 ID는 UUID 형식이어야 합니다.")
        String bookId,

        @Schema(description = "검색 키워드(작성자 닉네임 | 내용)", example = "홍길동")
        String keyword,

        @Schema(description = "정렬 기준(createdAt | rating)", defaultValue = "createdAt", example = "createdAt")
        @Pattern(regexp = "^(createdAt|rating)$", message = "정렬 기준은 createdAt 또는 rating이어야 합니다.")
        String orderBy,

        @Schema(description = "정렬 방향", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC", example = "DESC")
        @Pattern(regexp = "^(ASC|DESC)$", message = "정렬 방향은 ASC 또는 DESC여야 합니다.")
        String direction,

        @Schema(description = "커서 페이지네이션 커서")
        String cursor,

        @Schema(description = "보조 커서(createdAt)")
        Instant after,

        @Schema(description = "페이지 크기", defaultValue = "50", example = "50")
        Integer limit,

        String requestUserId
) {
    public CursorPageRequestReviewDto {
        if (orderBy == null) {
            orderBy = "createdAt";
        }
        if (direction == null) {
            direction = "DESC";
        }
        if (limit == null) {
            limit = 50;
        }
    }
}

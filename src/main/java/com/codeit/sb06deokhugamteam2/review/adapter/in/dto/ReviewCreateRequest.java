package com.codeit.sb06deokhugamteam2.review.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

@Schema(description = "리뷰 등록")
public record ReviewCreateRequest(
        @Schema(format = "uuid")
        @NotNull(message = "도서 ID는 필수입니다.") @UUID(message = "도서 ID는 UUID 형식이어야 합니다.")
        String bookId,

        @Schema(format = "uuid")
        @NotNull(message = "사용자 ID는 필수입니다.") @UUID(message = "사용자 ID는 UUID 형식이어야 합니다.")
        String userId,

        @NotNull(message = "리뷰 내용은 필수입니다.")
        String content,

        @Schema(minimum = "1", maximum = "5")
        @NotNull(message = "평점은 필수입니다.") @Range(min = 1, max = 5, message = "평점은 1에서 5 사이여야 합니다.")
        Integer rating
) {
}

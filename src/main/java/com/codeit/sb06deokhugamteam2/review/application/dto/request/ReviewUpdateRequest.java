package com.codeit.sb06deokhugamteam2.review.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Schema(description = "수정할 리뷰 정보")
public record ReviewUpdateRequest(
        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(min = 1, max = 500, message = "리뷰 내용은 1자 이상 500자 이하여야 합니다.")
        String content,

        @Schema(minimum = "1", maximum = "5")
        @NotNull(message = "평점은 필수입니다.") @Range(min = 1, max = 5, message = "평점은 1에서 5 사이여야 합니다.")
        Integer rating
) {
}

package com.codeit.sb06deokhugamteam2.review.application.dto;

import java.util.List;

public record ReviewSummary(
        List<ReviewDetail> content,
        String nextCursor,
        String nextAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}

package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;

import java.util.Optional;
import java.util.UUID;

public interface QueryReviewPort {

    Optional<ReviewDto> findById(UUID reviewId, UUID requestUserId);

    CursorPageResponseReviewDto findAll(CursorPageRequestReviewDto request, UUID requestUserId);
}

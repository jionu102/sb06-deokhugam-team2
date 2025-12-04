package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.port.in.DeleteReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteReviewService implements DeleteReviewUseCase {

    private final ReviewService reviewService;

    public DeleteReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void deleteReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        reviewService.hideReview(reviewId, requestUserId);
    }

    @Override
    public void hardDeleteReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        reviewService.deleteReview(reviewId, requestUserId);
    }
}

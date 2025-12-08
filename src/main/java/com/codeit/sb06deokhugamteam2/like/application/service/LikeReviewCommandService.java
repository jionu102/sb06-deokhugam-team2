package com.codeit.sb06deokhugamteam2.like.application.service;

import com.codeit.sb06deokhugamteam2.like.application.port.in.CancelReviewLikeUseCase;
import com.codeit.sb06deokhugamteam2.like.application.port.in.DeleteReviewLikesUseCase;
import com.codeit.sb06deokhugamteam2.like.application.port.in.LikeReviewUseCase;
import com.codeit.sb06deokhugamteam2.like.application.port.out.SaveLikeReviewPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LikeReviewCommandService
        implements DeleteReviewLikesUseCase, LikeReviewUseCase, CancelReviewLikeUseCase {

    private final SaveLikeReviewPort saveLikeReviewPort;

    public LikeReviewCommandService(SaveLikeReviewPort saveLikeReviewPort) {
        this.saveLikeReviewPort = saveLikeReviewPort;
    }

    @Override
    public void deleteReviews(UUID reviewId) {
        saveLikeReviewPort.delete(reviewId);
    }

    @Override
    public void likeReview(UUID reviewId, UUID userId, Instant likedAt) {
        saveLikeReviewPort.save(reviewId, userId, likedAt);
    }

    @Override
    public void cancelReviewLike(UUID reviewId, UUID userId) {
        saveLikeReviewPort.delete(reviewId, userId);
    }
}

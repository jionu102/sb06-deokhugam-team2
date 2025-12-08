package com.codeit.sb06deokhugamteam2.like.adapter.in.event;

import com.codeit.sb06deokhugamteam2.like.application.port.in.CancelReviewLikeUseCase;
import com.codeit.sb06deokhugamteam2.like.application.port.in.DeleteReviewLikesUseCase;
import com.codeit.sb06deokhugamteam2.like.application.port.in.LikeReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewDeletedEvent;
import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewLikeCanceledEvent;
import com.codeit.sb06deokhugamteam2.review.domain.event.ReviewLikedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LikeReviewEventHandler {

    private final DeleteReviewLikesUseCase deleteReviewLikesUseCase;
    private final LikeReviewUseCase likeReviewUseCase;
    private final CancelReviewLikeUseCase cancelReviewLikeUseCase;

    public LikeReviewEventHandler(
            DeleteReviewLikesUseCase deleteReviewLikesUseCase,
            LikeReviewUseCase likeReviewUseCase,
            CancelReviewLikeUseCase cancelReviewLikeUseCase
    ) {
        this.deleteReviewLikesUseCase = deleteReviewLikesUseCase;
        this.likeReviewUseCase = likeReviewUseCase;
        this.cancelReviewLikeUseCase = cancelReviewLikeUseCase;
    }

    @EventListener(ReviewDeletedEvent.class)
    public void handleReviewDeletedEvent(ReviewDeletedEvent event) {
        deleteReviewLikesUseCase.deleteReviews(event.reviewId());
    }

    @EventListener(ReviewLikedEvent.class)
    public void handleReviewLikedEvent(ReviewLikedEvent event) {
        likeReviewUseCase.likeReview(event.reviewId(), event.userId(), event.likedAt());
    }
    
    @EventListener(ReviewLikeCanceledEvent.class)
    public void handleReviewLikeCanceledEvent(ReviewLikeCanceledEvent event) {
        cancelReviewLikeUseCase.cancelReviewLike(event.reviewId(), event.userId());
    }
}

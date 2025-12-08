package com.codeit.sb06deokhugamteam2.review.domain.service;

import com.codeit.sb06deokhugamteam2.review.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewService {

    public void registerReview(ReviewDomain review, ReviewBookDomain book) {
        book.increaseReviewCount().plusReviewRating(review.rating());
    }

    public void hideReview(ReviewDomain review, UUID requestUserId, ReviewBookDomain book) {
        review.verifyOwner(requestUserId).hide();
        book.decreaseReviewCount().minusReviewRating(review.rating());
    }

    public void deleteReview(ReviewDomain review, UUID requestUserId, ReviewBookDomain book) {
        review.verifyOwner(requestUserId);
        if (!review.isHidden()) {
            book.decreaseReviewCount().minusReviewRating(review.rating());
        }
        review.delete();
    }

    public void editReview(
            ReviewDomain review,
            ReviewRatingDomain newRating,
            ReviewContentDomain newContent,
            UUID requestUserId,
            ReviewBookDomain book
    ) {
        ReviewRatingDomain oldRating = review.rating();
        review.verifyOwner(requestUserId).edit(newRating, newContent);
        book.minusReviewRating(oldRating).plusReviewRating(newRating);
    }

    public void toggleReviewLike(ReviewDomain review, ReviewLikeDomain reviewLike) {
        reviewLike.toggleLike();
        if (reviewLike.isLiked()) {
            review.increaseReviewLike();
        } else {
            review.decreaseReviewLike();
        }
    }
}

package com.codeit.sb06deokhugamteam2.review.domain;

import com.codeit.sb06deokhugamteam2.review.domain.exception.AlreadyExistsReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewBookRepository;
import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewRepository;
import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewBookRepository bookRepository;
    private final ReviewUserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(
            ReviewBookRepository bookRepository,
            ReviewUserRepository userRepository,
            ReviewRepository reviewRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public ReviewDomain registerReview(UUID bookId, UUID userId, ReviewRating rating, ReviewContent content) {
        ReviewBookDomain book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ReviewBookNotFoundException(bookId));
        if (!userRepository.existsById(userId)) {
            throw new ReviewUserNotFoundException(userId);
        }
        if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new AlreadyExistsReviewException(bookId);
        }

        ReviewDomain review = ReviewDomain.write(bookId, userId, rating, content);
        book.increaseReviewCount().plusReviewRating(review.rating());

        reviewRepository.save(review);
        bookRepository.update(book);

        return review;
    }

    public void hideReview(UUID reviewId, UUID requestUserId) {
        ReviewDomain review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));

        review.verifyOwner(requestUserId);
        book.decreaseReviewCount().minusReviewRating(review.rating());

        reviewRepository.delete(review);
        bookRepository.update(book);
    }

    public void deleteReview(UUID reviewId, UUID requestUserId) {
        ReviewDomain review = reviewRepository.findByIdWithoutDeleted(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));

        review.verifyOwner(requestUserId);
        book.decreaseReviewCount().minusReviewRating(review.rating());

        reviewRepository.hardDelete(review);
        bookRepository.update(book);
    }

    public ReviewDomain editReview(UUID reviewId, UUID requestUserId, ReviewRating rating, ReviewContent content) {
        ReviewDomain review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));

        ReviewRating oldRating = review.rating();
        ReviewRating newRating = rating;
        review.verifyOwner(requestUserId).edit(newRating, content);
        book.minusReviewRating(oldRating).plusReviewRating(newRating);

        reviewRepository.update(review);
        bookRepository.update(book);

        return review;
    }
}

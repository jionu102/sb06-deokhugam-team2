package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewUpdateRequest;
import com.codeit.sb06deokhugamteam2.review.application.port.in.CreateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.DeleteReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.UpdateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewBookRepositoryPort;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewRepositoryPort;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewUserRepositoryPort;
import com.codeit.sb06deokhugamteam2.review.domain.*;
import com.codeit.sb06deokhugamteam2.review.domain.exception.AlreadyExistsReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReviewCommandService implements CreateReviewUseCase, UpdateReviewUseCase, DeleteReviewUseCase {

    private final ReviewService reviewService;
    private final ReviewBookRepositoryPort bookRepository;
    private final ReviewUserRepositoryPort userRepository;
    private final ReviewRepositoryPort reviewRepository;

    public ReviewCommandService(
            ReviewService reviewService,
            ReviewBookRepositoryPort bookRepository,
            ReviewUserRepositoryPort userRepository,
            ReviewRepositoryPort reviewRepository
    ) {
        this.reviewService = reviewService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewDto createReview(ReviewCreateRequest requestBody) {
        UUID bookId = UUID.fromString(requestBody.bookId());
        UUID userId = UUID.fromString(requestBody.userId());
        var rating = new ReviewRating(requestBody.rating());
        var content = new ReviewContent(requestBody.content());

        ReviewDomain review = ReviewDomain.create(bookId, userId, rating, content);
        ReviewBookDomain book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ReviewBookNotFoundException(bookId));
        if (!userRepository.existsById(userId)) {
            throw new ReviewUserNotFoundException(userId);
        }
        if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new AlreadyExistsReviewException(bookId);
        }
        reviewService.registerReview(review, book);
        reviewRepository.save(review);
        bookRepository.update(book);

        return reviewRepository.findById(review.id(), null)
                .orElseThrow(() -> new ReviewNotFoundException(review.id()));
    }

    @Override
    public void hideReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        ReviewDomain review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.deleteReview(review, requestUserId, book);
        reviewRepository.softDelete(review);
        bookRepository.update(book);
    }

    @Override
    public void deleteReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        ReviewDomain review = reviewRepository.findByIdWithoutDeleted(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.deleteReview(review, requestUserId, book);
        reviewRepository.hardDelete(review);
        bookRepository.update(book);
    }

    @Override
    public ReviewDto updateReview(String path, String header, ReviewUpdateRequest requestBody) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);
        var newRating = new ReviewRating(requestBody.rating());
        var newContent = new ReviewContent(requestBody.content());

        ReviewDomain review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = bookRepository.findById(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.editReview(review, newRating, newContent, requestUserId, book);
        reviewRepository.update(review);
        bookRepository.update(book);

        return reviewRepository.findById(review.id(), requestUserId)
                .orElseThrow(() -> new ReviewNotFoundException(review.id()));
    }
}

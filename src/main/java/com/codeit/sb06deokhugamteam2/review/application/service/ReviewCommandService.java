package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewUpdateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewLikeDto;
import com.codeit.sb06deokhugamteam2.review.application.port.in.CreateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.DeleteReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.ToggleReviewLikeUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.UpdateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.out.*;
import com.codeit.sb06deokhugamteam2.review.domain.exception.AlreadyExistsReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.model.*;
import com.codeit.sb06deokhugamteam2.review.domain.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReviewCommandService
        implements CreateReviewUseCase, UpdateReviewUseCase, DeleteReviewUseCase, ToggleReviewLikeUseCase {

    private final ReviewService reviewService;
    private final ReviewEventPublisher eventPublisher;
    private final LoadReviewUserPort loadUserPort;
    private final SaveReviewUserPort saveUserPort;
    private final LoadReviewBookPort loadBookPort;
    private final SaveReviewBookPort saveBookPort;
    private final LoadReviewPort loadReviewPort;
    private final SaveReviewPort saveReviewPort;
    private final SaveReviewCommentPort saveCommentPort;
    private final LoadReviewLikePort loadLikePort;
    private final SaveReviewNotificationPort saveReviewNotificationPort;

    public ReviewCommandService(
            ReviewService reviewService,
            ReviewEventPublisher eventPublisher,
            LoadReviewUserPort loadUserPort,
            SaveReviewUserPort saveUserPort,
            LoadReviewBookPort loadBookPort,
            SaveReviewBookPort saveBookPort,
            LoadReviewPort loadReviewPort,
            SaveReviewPort saveReviewPort,
            SaveReviewCommentPort saveCommentPort,
            LoadReviewLikePort loadLikePort,
            SaveReviewNotificationPort saveReviewNotificationPort
    ) {
        this.reviewService = reviewService;
        this.eventPublisher = eventPublisher;
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.loadBookPort = loadBookPort;
        this.saveBookPort = saveBookPort;
        this.loadReviewPort = loadReviewPort;
        this.saveReviewPort = saveReviewPort;
        this.saveCommentPort = saveCommentPort;
        this.loadLikePort = loadLikePort;
        this.saveReviewNotificationPort = saveReviewNotificationPort;
    }

    @Override
    public ReviewDto createReview(ReviewCreateRequest requestBody) {
        UUID bookId = UUID.fromString(requestBody.bookId());
        UUID userId = UUID.fromString(requestBody.userId());
        var rating = new ReviewRatingDomain(requestBody.rating());
        var content = new ReviewContentDomain(requestBody.content());
        ReviewDomain review = ReviewDomain.create(bookId, userId, rating, content);

        if (!loadUserPort.existsById(userId)) {
            throw new ReviewUserNotFoundException(userId);
        }
        if (loadReviewPort.existsByBookIdAndUserId(bookId, userId)) {
            throw new AlreadyExistsReviewException(bookId);
        }
        ReviewBookDomain book = loadBookPort.findByIdForUpdate(bookId)
                .orElseThrow(() -> new ReviewBookNotFoundException(bookId));
        reviewService.registerReview(review, book);
        saveReviewPort.save(review.toSnapshot());
        saveBookPort.update(book);
        review.events().forEach(eventPublisher::publish);
        review.clearEvents();

        return loadReviewPort.findById(review.id(), null)
                .orElseThrow(() -> new ReviewNotFoundException(review.id()));
    }

    @Override
    public void softDeleteReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        ReviewDomain review = saveReviewPort.findById(reviewId)
                .map(ReviewDomain::from)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = loadBookPort.findByIdForUpdate(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.hideReview(review, requestUserId, book);
        saveReviewPort.softDelete(review.id());
        saveCommentPort.softDelete(review.id());
        saveBookPort.update(book);
        review.events().forEach(eventPublisher::publish);
        review.clearEvents();
    }

    @Override
    public void deleteReview(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);

        ReviewDomain review = saveReviewPort.findByIdWithoutDeleted(reviewId)
                .map(ReviewDomain::from)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = loadBookPort.findByIdForUpdate(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.deleteReview(review, requestUserId, book);
        saveReviewPort.hardDelete(review.id());
        saveBookPort.update(book);
        review.events().forEach(eventPublisher::publish);
        review.clearEvents();
    }

    @Override
    public ReviewDto updateReview(String path, String header, ReviewUpdateRequest requestBody) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);
        var newRating = new ReviewRatingDomain(requestBody.rating());
        var newContent = new ReviewContentDomain(requestBody.content());

        ReviewDomain review = saveReviewPort.findById(reviewId)
                .map(ReviewDomain::from)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewBookDomain book = loadBookPort.findByIdForUpdate(review.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(review.bookId()));
        reviewService.editReview(review, newRating, newContent, requestUserId, book);
        saveReviewPort.update(review.toSnapshot());
        saveBookPort.update(book);
        review.events().forEach(eventPublisher::publish);
        review.clearEvents();

        return loadReviewPort.findById(reviewId, requestUserId)
                .orElseThrow(() -> new ReviewNotFoundException(review.id()));
    }

    @Override
    public ReviewLikeDto toggleReviewLike(String path, String header) {
        UUID reviewId = UUID.fromString(path);
        UUID userId = UUID.fromString(header);

        ReviewDomain review = saveReviewPort.findByIdForUpdate(reviewId)
                .map(ReviewDomain::from)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        ReviewUserDomain user = saveUserPort.findById(userId)
                .orElseThrow(() -> new ReviewUserNotFoundException(userId));
        ReviewLikeDomain reviewLike = loadLikePort.findById(reviewId, userId)
                .orElseGet(() -> new ReviewLikeDomain(reviewId, userId, false));
        reviewService.toggleReviewLike(review, reviewLike);
        saveReviewPort.update(review.toSnapshot());
        ReviewLikeNotificationDomain notification = ReviewLikeNotificationDomain.create(
                reviewId, user.id(), review.content().value(), user.nickname());
        saveReviewNotificationPort.sendNotification(notification);
        review.events().forEach(eventPublisher::publish);
        review.clearEvents();

        return new ReviewLikeDto(reviewLike.reviewId(), reviewLike.userId(), reviewLike.isLiked());
    }
}

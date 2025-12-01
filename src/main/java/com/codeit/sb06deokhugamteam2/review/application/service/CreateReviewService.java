package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.port.in.CreateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.in.command.CreateReviewCommand;
import com.codeit.sb06deokhugamteam2.review.application.port.out.BookRepository;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewRepository;
import com.codeit.sb06deokhugamteam2.review.application.port.out.UserRepository;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.exception.DuplicateReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateReviewService implements CreateReviewUseCase {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public CreateReviewService(
            BookRepository bookRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public ReviewDetail createReview(CreateReviewCommand command) {
        UUID bookId = command.bookId();
        UUID userId = command.userId();

        if (!bookRepository.existsById(bookId)) {
            throw new ReviewBookNotFoundException(bookId);
        }
        if (!userRepository.existsById(userId)) {
            throw new ReviewUserNotFoundException(userId);
        }
        if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new DuplicateReviewException(bookId);
        }

        ReviewDomain newReview = ReviewDomain.create(command);
        reviewRepository.addReview(newReview);
        bookRepository.updateOnReviewCreation(newReview.bookId(), newReview.rating());
        return reviewRepository.findReviewDetailById(newReview.id());
    }
}

package com.codeit.sb06deokhugamteam2.review.application;

import com.codeit.sb06deokhugamteam2.review.domain.*;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewException.BookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewException.DuplicateReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewException.UserNotFoundException;
import com.codeit.sb06deokhugamteam2.review.infra.ReviewMapper;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BasicReviewService implements ReviewService {

    // Infrastructures
    private final ReviewMapper reviewMapper;

    // Domains
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BasicReviewService(
            ReviewMapper reviewMapper,
            ReviewRepository reviewRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewDto createReview(ReviewCreateRequest request) {
        ReviewCreationCommand command = reviewMapper.toCreationCommand(request);
        var review = new ReviewDomain(command);
        UUID bookId = review.bookId();
        UUID userId = review.userId();
        bookRepository.exists(bookId, BookNotFoundException::new);
        userRepository.exists(userId, UserNotFoundException::new);
        reviewRepository.existsByBookIdAndUserId(bookId, userId, DuplicateReviewException::new);
        ReviewDetail reviewDetail = reviewRepository.save(review);
        return reviewMapper.toDto(reviewDetail);
    }
}

package com.codeit.sb06deokhugamteam2.review.application;

import com.codeit.sb06deokhugamteam2.review.domain.*;
import com.codeit.sb06deokhugamteam2.review.domain.exception.DuplicateReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import com.codeit.sb06deokhugamteam2.review.infra.ReviewMapper;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BasicReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

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
        ReviewBook book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ReviewBookNotFoundException(request.bookId()));
        ReviewUser user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ReviewUserNotFoundException(request.userId()));
        reviewRepository.findByBookIdAndUserId(request.bookId(), request.userId())
                .ifPresent(bookId -> { throw new DuplicateReviewException(bookId); });
        ReviewDomain newReview = reviewMapper.toDomain(request);
        reviewRepository.save(newReview);
        bookRepository.updateOnReviewCreation(book.id(), newReview.rating());
        return reviewMapper.toDto(newReview, book, user);
    }
}

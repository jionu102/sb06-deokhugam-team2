package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewUpdateRequest;
import com.codeit.sb06deokhugamteam2.review.application.port.in.UpdateReviewUseCase;
import com.codeit.sb06deokhugamteam2.review.application.port.out.QueryReviewPort;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewContent;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewRating;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewService;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateReviewService implements UpdateReviewUseCase {

    private final ReviewService reviewService;
    private final QueryReviewPort queryReviewPort;

    public UpdateReviewService(ReviewService reviewService, QueryReviewPort queryReviewPort) {
        this.reviewService = reviewService;
        this.queryReviewPort = queryReviewPort;
    }

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public ReviewDto updateReview(String path, String header, ReviewUpdateRequest requestBody) {
        UUID reviewId = UUID.fromString(path);
        UUID requestUserId = UUID.fromString(header);
        var rating = new ReviewRating(requestBody.rating());
        var content = new ReviewContent(requestBody.content());

        ReviewDomain updated = reviewService.editReview(reviewId, requestUserId, rating, content);
        return queryReviewPort.findById(updated.id(), requestUserId)
                .orElseThrow(() -> new ReviewNotFoundException(updated.id()));
    }
}

package com.codeit.sb06deokhugamteam2.review.application.service;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.GetReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetReviewService implements GetReviewQuery {

    private final ReviewRepository reviewRepository;

    public GetReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewSummary readReviews(ReviewPaginationQuery query) {
        return reviewRepository.findReviewSummary(query);
    }

    @Override
    public ReviewDetail readReview(ReviewQuery query) {
        return reviewRepository.findReviewDetail(query);
    }
}

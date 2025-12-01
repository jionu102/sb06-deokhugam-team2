package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewSummary;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewPaginationQuery;
import com.codeit.sb06deokhugamteam2.review.application.port.in.query.ReviewQuery;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;

import java.util.UUID;

public interface ReviewRepository {

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

    void addReview(ReviewDomain review);

    ReviewDetail findReviewDetailById(UUID reviewId);

    ReviewSummary findReviewSummary(ReviewPaginationQuery query);

    ReviewDetail findReviewDetail(ReviewQuery query);
}

package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;

public interface CreateReviewUseCase {

    ReviewDto createReview(ReviewCreateRequest requestBody);
}

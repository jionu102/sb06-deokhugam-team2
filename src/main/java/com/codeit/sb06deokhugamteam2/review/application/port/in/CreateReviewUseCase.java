package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.request.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;

public interface CreateReviewUseCase {

    ReviewDto createReview(ReviewCreateRequest requestBody);
}

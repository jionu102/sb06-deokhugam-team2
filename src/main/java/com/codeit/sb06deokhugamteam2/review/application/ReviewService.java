package com.codeit.sb06deokhugamteam2.review.application;

import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewCreateRequest;
import com.codeit.sb06deokhugamteam2.review.infra.web.dto.ReviewDto;

public interface ReviewService {

    ReviewDto createReview(ReviewCreateRequest request);
}

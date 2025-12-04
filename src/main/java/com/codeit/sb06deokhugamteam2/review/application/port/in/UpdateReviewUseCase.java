package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewUpdateRequest;

public interface UpdateReviewUseCase {

    ReviewDto updateReview(String path, String header, ReviewUpdateRequest requestBody);
}

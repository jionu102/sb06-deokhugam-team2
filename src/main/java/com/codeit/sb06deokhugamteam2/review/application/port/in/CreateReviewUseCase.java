package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDetail;
import com.codeit.sb06deokhugamteam2.review.application.port.in.command.CreateReviewCommand;

public interface CreateReviewUseCase {

    ReviewDetail createReview(CreateReviewCommand request);
}

package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewLikeDto;

public interface ToggleReviewLikeUseCase {

    ReviewLikeDto toggleReviewLike(String path, String header);
}

package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.request.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.response.ReviewDto;

public interface GetReviewUseCase {

    CursorPageResponseReviewDto readReviews(CursorPageRequestReviewDto query, String header);

    ReviewDto readReview(String path, String header);
}

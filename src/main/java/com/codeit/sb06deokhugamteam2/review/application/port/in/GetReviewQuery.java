package com.codeit.sb06deokhugamteam2.review.application.port.in;

import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageResponseReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;

public interface GetReviewQuery {

    CursorPageResponseReviewDto readReviews(CursorPageRequestReviewDto query, String header);

    ReviewDto readReview(String path, String header);
}

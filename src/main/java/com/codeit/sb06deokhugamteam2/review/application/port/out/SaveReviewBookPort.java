package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewBookDomain;

public interface SaveReviewBookPort {

    void update(ReviewBookDomain book);
}

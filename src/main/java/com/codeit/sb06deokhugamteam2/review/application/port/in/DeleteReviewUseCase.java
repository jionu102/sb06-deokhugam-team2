package com.codeit.sb06deokhugamteam2.review.application.port.in;

public interface DeleteReviewUseCase {

    void softDeleteReview(String path, String header);

    void deleteReview(String path, String header);
}

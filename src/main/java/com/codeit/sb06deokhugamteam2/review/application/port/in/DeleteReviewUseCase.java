package com.codeit.sb06deokhugamteam2.review.application.port.in;

public interface DeleteReviewUseCase {

    void deleteReview(String path, String header);

    void hardDeleteReview(String path, String header);
}

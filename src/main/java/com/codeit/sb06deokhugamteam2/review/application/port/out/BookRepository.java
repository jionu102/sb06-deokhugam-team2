package com.codeit.sb06deokhugamteam2.review.application.port.out;

import java.util.UUID;

public interface BookRepository {

    boolean existsById(UUID bookId);

    void updateOnReviewCreation(UUID bookId, int rating);
}

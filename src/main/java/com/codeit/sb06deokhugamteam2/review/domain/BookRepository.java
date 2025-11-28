package com.codeit.sb06deokhugamteam2.review.domain;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository {

    Optional<ReviewBook> findById(UUID bookId);

    void updateOnReviewCreation(UUID bookId, int rating);
}

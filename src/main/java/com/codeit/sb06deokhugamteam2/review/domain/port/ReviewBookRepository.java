package com.codeit.sb06deokhugamteam2.review.domain.port;

import com.codeit.sb06deokhugamteam2.review.domain.ReviewBookDomain;

import java.util.Optional;
import java.util.UUID;

public interface ReviewBookRepository {

    Optional<ReviewBookDomain> findById(UUID bookId);

    void update(ReviewBookDomain book);
}

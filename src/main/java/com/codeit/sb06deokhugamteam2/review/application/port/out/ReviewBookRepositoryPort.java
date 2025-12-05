package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.ReviewBookDomain;

import java.util.Optional;
import java.util.UUID;

public interface ReviewBookRepositoryPort {

    Optional<ReviewBookDomain> findById(UUID bookId);

    void update(ReviewBookDomain book);
}

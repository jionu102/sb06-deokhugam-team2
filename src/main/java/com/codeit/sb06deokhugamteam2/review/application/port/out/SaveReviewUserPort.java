package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewUserDomain;

import java.util.Optional;
import java.util.UUID;

public interface SaveReviewUserPort {

    Optional<ReviewUserDomain> findById(UUID userId);
}

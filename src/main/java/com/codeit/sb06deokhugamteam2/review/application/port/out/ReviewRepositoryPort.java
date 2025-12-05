package com.codeit.sb06deokhugamteam2.review.application.port.out;

import com.codeit.sb06deokhugamteam2.review.application.dto.CursorPageRequestReviewDto;
import com.codeit.sb06deokhugamteam2.review.application.dto.ReviewDto;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepositoryPort {

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

    void save(ReviewDomain review);

    Optional<ReviewDto> findById(UUID reviewId, UUID requestUserId);

    List<ReviewDto> findAll(CursorPageRequestReviewDto request, UUID requestUserId);

    long count(String userId, String bookId, String keyword);

    Optional<ReviewDomain> findById(UUID reviewId);

    void softDelete(ReviewDomain review);

    Optional<ReviewDomain> findByIdWithoutDeleted(UUID reviewId);

    void hardDelete(ReviewDomain review);

    void update(ReviewDomain review);
}

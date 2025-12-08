package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.like.adapter.out.entity.ReviewLike;
import com.codeit.sb06deokhugamteam2.like.adapter.out.entity.ReviewLikeId;
import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewLikePort;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewLikeDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ReviewLikeJpaRepository implements LoadReviewLikePort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ReviewLikeDomain> findById(UUID reviewId, UUID userId) {
        var reviewLikeIdEntity = new ReviewLikeId().reviewId(reviewId).userId(userId);
        ReviewLike reviewLikeEntity = em.find(ReviewLike.class, reviewLikeIdEntity);
        return Optional.ofNullable(reviewLikeEntity)
                .map(rle -> new ReviewLikeDomain(reviewId, userId, true));
    }
}

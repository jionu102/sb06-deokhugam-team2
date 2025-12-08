package com.codeit.sb06deokhugamteam2.like.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.like.adapter.out.entity.ReviewLike;
import com.codeit.sb06deokhugamteam2.like.adapter.out.entity.ReviewLikeId;
import com.codeit.sb06deokhugamteam2.like.application.port.out.SaveLikeReviewPort;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPADeleteClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

import static com.codeit.sb06deokhugamteam2.like.adapter.out.entity.QReviewLike.reviewLike;

@Repository
public class LikeReviewJpaRepository implements SaveLikeReviewPort {

    @PersistenceContext
    private EntityManager em;

    private JPADeleteClause delete(EntityPath<?> entity) {
        return new JPADeleteClause(em, entity);
    }

    @Override
    public void delete(UUID reviewId) {
        delete(reviewLike).where(reviewLike.review.id.eq(reviewId)).execute();
    }

    @Override
    public void save(UUID reviewId, UUID userId, Instant likedAt) {
        ReviewLikeId reviewLikeIdEntity = new ReviewLikeId().userId(userId).reviewId(reviewId);
        User userEntity = em.getReference(User.class, userId);
        Review reviewEntity = em.getReference(Review.class, reviewId);

        var reviewLikeEntity = new ReviewLike()
                .id(reviewLikeIdEntity)
                .user(userEntity)
                .review(reviewEntity)
                .likedAt(likedAt);

        em.persist(reviewLikeEntity);
    }

    @Override
    public void delete(UUID reviewId, UUID userId) {
        ReviewLikeId reviewLikeIdEntity = new ReviewLikeId().reviewId(reviewId).userId(userId);
        ReviewLike reviewLikeEntity = em.getReference(ReviewLike.class, reviewLikeIdEntity);
        em.remove(reviewLikeEntity);
    }
}

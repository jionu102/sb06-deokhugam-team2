package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.review.application.port.out.SaveReviewCommentPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ReviewCommentJpaRepository implements SaveReviewCommentPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void softDelete(UUID reviewId) {
        em.createNativeQuery("""
                        UPDATE comments
                        SET deleted = true
                        WHERE review_id = :reviewId
                        """)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
    }
}

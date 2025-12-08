package com.codeit.sb06deokhugamteam2.review.adapter.out.repository;

import com.codeit.sb06deokhugamteam2.book.entity.BookStats;
import com.codeit.sb06deokhugamteam2.review.application.port.out.LoadReviewBookPort;
import com.codeit.sb06deokhugamteam2.review.application.port.out.SaveReviewBookPort;
import com.codeit.sb06deokhugamteam2.review.domain.model.ReviewBookDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ReviewBookJpaRepository implements LoadReviewBookPort, SaveReviewBookPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ReviewBookDomain> findByIdForUpdate(UUID bookId) {
        BookStats bookStatsEntity = em.find(BookStats.class, bookId, LockModeType.PESSIMISTIC_WRITE);
        if (bookStatsEntity == null) {
            return Optional.empty();
        }

        UUID id = bookStatsEntity.getBookId();
        int reviewCount = bookStatsEntity.getReviewCount();
        int ratingSum = bookStatsEntity.getRatingSum();
        var book = new ReviewBookDomain(id, reviewCount, ratingSum);

        return Optional.of(book);
    }

    @Override
    public void update(ReviewBookDomain book) {
        BookStats bookStatEntity = em.find(BookStats.class, book.id());
        bookStatEntity.setReviewCount(book.reviewCount());
        bookStatEntity.setRatingSum(book.ratingSum());
    }
}

package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.BookStats;
import com.codeit.sb06deokhugamteam2.review.application.port.out.ReviewBookRepositoryPort;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewBookDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ReviewBookJpaRepositoryAdapter implements ReviewBookRepositoryPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ReviewBookDomain> findById(UUID bookId) {
        BookStats bookStatsEntity = em.find(BookStats.class, bookId);
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
    @Transactional
    public void update(ReviewBookDomain book) {
        BookStats bookStatsEntity = em.find(BookStats.class, book.id());
        bookStatsEntity.setReviewCount(book.reviewCount());
        bookStatsEntity.setRatingSum(book.ratingSum());
    }
}

package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewBookDomain;
import com.codeit.sb06deokhugamteam2.review.domain.port.ReviewBookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class ReviewBookJpaRepository implements ReviewBookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ReviewBookDomain> findById(UUID bookId) {
        Book bookEntity = em.find(Book.class, bookId);
        if (bookEntity == null) {
            return Optional.empty();
        }

        UUID id = bookEntity.getId();
        int reviewCount = bookEntity.getReviewCount();
        int ratingSum = bookEntity.getRatingSum();
        var book = new ReviewBookDomain(id, reviewCount, ratingSum);

        return Optional.of(book);
    }

    @Override
    @Transactional
    public void update(ReviewBookDomain book) {
        Book bookEntity = em.find(Book.class, book.id());
        bookEntity.setReviewCount(book.reviewCount());
        bookEntity.setRatingSum(book.ratingSum());
    }
}

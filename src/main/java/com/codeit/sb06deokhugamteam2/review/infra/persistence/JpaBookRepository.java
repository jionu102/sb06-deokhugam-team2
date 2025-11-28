package com.codeit.sb06deokhugamteam2.review.infra.persistence;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.domain.BookRepository;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewBook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ReviewBook> findById(UUID bookId) {
        Book book = em.find(Book.class, bookId);
        if (book == null) {
            return Optional.empty();
        }
        var reviewBook = new ReviewBook(book.getId(), book.getTitle(), book.getThumbnailUrl());
        return Optional.of(reviewBook);
    }

    @Override
    public void updateOnReviewCreation(UUID bookId, int rating) {
        Book book = em.find(Book.class, bookId);
        book.incrementReviewCount();
        book.plusRating(rating);
    }
}

package com.codeit.sb06deokhugamteam2.review.adapter.out;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.application.port.out.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class BookJpaRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean existsById(UUID bookId) {
        Book found = em.find(Book.class, bookId);
        return found != null;
    }

    @Override
    @Transactional
    public void updateOnReviewCreation(UUID bookId, int rating) {
        Book book = em.find(Book.class, bookId);
        book.incrementReviewCount();
        book.plusRating(rating);
    }
}

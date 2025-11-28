package com.codeit.sb06deokhugamteam2.review.infra.persistence;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.review.domain.BookRepository;
import com.codeit.sb06deokhugamteam2.review.domain.ReviewException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Component
@Transactional(readOnly = true)
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void exists(UUID bookId, Function<UUID, ? extends ReviewException> exceptionFunction) {
        Book book = em.find(Book.class, bookId);
        if (book == null) {
            throw exceptionFunction.apply(bookId);
        }
    }
}

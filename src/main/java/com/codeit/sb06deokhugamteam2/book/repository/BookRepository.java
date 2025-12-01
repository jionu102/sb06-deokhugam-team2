package com.codeit.sb06deokhugamteam2.book.repository;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {
    @Query("""
        SELECT COUNT(*)
        FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    long countByKeyword(String keyword);

    List<Book> findAllByCreatedAtAfter(Instant since);

    boolean existsByIsbn(String isbn);
}

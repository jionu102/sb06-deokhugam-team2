package com.codeit.sb06deokhugamteam2.book.repository;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {
    @Query("""
                SELECT COUNT(*)
                FROM Book b
                WHERE
                    (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    long countBooksByKeyword(String keyword);

    Optional<Book> findByIsbn(String isbn);

    // @SoftDelete
    @Modifying
    @Query("DELETE FROM Book b WHERE b.id = :bookId")
    void deleteSoftById(UUID bookId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM books WHERE id = :bookId AND deleted = true", nativeQuery = true)
    void deleteHardById(UUID bookId);
}

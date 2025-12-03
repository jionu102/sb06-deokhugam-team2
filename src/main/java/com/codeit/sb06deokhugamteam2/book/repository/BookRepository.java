package com.codeit.sb06deokhugamteam2.book.repository;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {
//    @Query("""
//                SELECT COUNT(*)
//                FROM Book b
//                WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//                LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//                LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND
//                b.deleted = false
//            """)
//    long countNotDeletedBooksByKeyword(String keyword);
//
//    long countByDeletedFalse();
//
//    Optional<Book> findByIsbnAndDeletedFalse(String isbn);

    // 논리삭제하면 영속성 컨텍스트에서 제거하기 위해 clearAutomatically = true 설정
    // todo: soft delete 관련 테스트 코드 작성 필요
    @Modifying(clearAutomatically = true)
    @Query("Delete FROM Book b WHERE b.id = :bookId")
    void deleteSoftById(UUID bookId);

    @Modifying
    @Query(value = "DELETE FROM book WHERE id = :bookId AND deleted = true", nativeQuery = true)
    void deleteHardById(UUID bookId);
}

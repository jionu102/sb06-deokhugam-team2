package com.codeit.sb06deokhugamteam2.book.service;

import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public void deleteSoft(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("도서를 찾을 수 없습니다: " + bookId));

//        book.getReviews().forEach(review -> {
//            review.setDeletedAsTrue();
//            review.getComments().forEach(Comment::setDeletedAsTrue);
//        });

        book.setDeletedAsTrue();
        bookRepository.save(book);
        log.info("도서 논리 삭제 완료: {}", bookId);
    }

    public void deleteHard(UUID bookId) {
        bookRepository.deleteById(bookId);
        log.info("도서 물리 삭제 완료: {}", bookId);
    }
}

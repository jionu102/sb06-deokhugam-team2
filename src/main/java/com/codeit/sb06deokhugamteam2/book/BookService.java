package com.codeit.sb06deokhugamteam2.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public void deleteSoft(UUID bookId) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setDeletedAsTrue();
            bookRepository.save(book);
            log.info("도서 논리 삭제 완료: {}", bookId);
        });
    }
}

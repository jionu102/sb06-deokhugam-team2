package com.codeit.sb06deokhugamteam2.book.service;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookImageCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.mapper.BookMapper;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BookException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final S3Storage s3Storage;
    private final BookMapper bookMapper;
    private final NaverSearchClient naverSearchClient;

    public BookDto create(BookCreateRequest bookCreateRequest, Optional<BookImageCreateRequest> optionalBookImageCreateRequest) {
        Book book = Book.builder()
                .isbn(bookCreateRequest.getIsbn())
                .title(bookCreateRequest.getTitle())
                .author(bookCreateRequest.getAuthor())
                .description(bookCreateRequest.getDescription())
                .publisher(bookCreateRequest.getPublisher())
                .publishedDate(bookCreateRequest.getPublishedDate())
                .build();

        Book savedBook = bookRepository.save(book);
        String thumbnailUrl = optionalBookImageCreateRequest.map(bookImageCreateRequest -> {
                    String key = savedBook.getId().toString() + "-" + bookImageCreateRequest.getOriginalFilename();
                    s3Storage.putThumbnail(key, bookImageCreateRequest.getBytes(), bookImageCreateRequest.getContentType());
                    return s3Storage.getThumbnail(key);
                }
        ).orElse(null);

        savedBook.updateThumbnailUrl(thumbnailUrl);

        return bookMapper.toDto(savedBook);
    }

    @Transactional(readOnly = true)
    public NaverBookDto info(String isbn) {
        return naverSearchClient.bookSearchByIsbn(isbn);
    }

    public BookDto update(UUID bookId, BookUpdateRequest bookUpdateRequest, Optional<BookImageCreateRequest> optionalBookImageCreateRequest) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(
                () -> new BookException(ErrorCode.NO_ID_VARIABLE,
                        Map.of("bookId", bookId),
                        HttpStatus.NOT_FOUND
                )
        );

        String thumbnailUrl = optionalBookImageCreateRequest.map(bookImageCreateRequest -> {
            if (findBook.getThumbnailUrl() != null) {
                String url = findBook.getThumbnailUrl();
                String oldKey = url.substring(url.lastIndexOf("/") + 1);
                s3Storage.deleteThumbnail(oldKey);
            }
            String newKey = findBook.getId().toString() + "-" + bookImageCreateRequest.getOriginalFilename();
            s3Storage.putThumbnail(newKey, bookImageCreateRequest.getBytes(), bookImageCreateRequest.getContentType());
            return s3Storage.getThumbnail(newKey);
        }).orElseGet(() -> {
            if (findBook.getThumbnailUrl() != null) {
                String url = findBook.getThumbnailUrl();
                String oldKey = url.substring(url.lastIndexOf("/") + 1);
                s3Storage.deleteThumbnail(oldKey);
            }
            return null;
        });

        findBook.updateAll(
                bookUpdateRequest.getTitle(),
                bookUpdateRequest.getAuthor(),
                bookUpdateRequest.getDescription(),
                bookUpdateRequest.getPublisher(),
                bookUpdateRequest.getPublishedDate(),
                thumbnailUrl
        );

        return bookMapper.toDto(findBook);
    }

    public void deleteSoft(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + bookId));

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
package com.codeit.sb06deokhugamteam2.book.service;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.data.PopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookImageCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponseBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.mapper.BookCursorMapper;
import com.codeit.sb06deokhugamteam2.book.mapper.BookMapper;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BookException;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.repository.DashboardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final DashboardRepository dashBoardRepository;
    private final S3Storage s3Storage;
    private final BookMapper bookMapper;
    private final BookCursorMapper bookCursorMapper;
    private final NaverSearchClient naverSearchClient;

    public BookDto create(BookCreateRequest bookCreateRequest, Optional<BookImageCreateRequest> optionalBookImageCreateRequest) {
        Book findBook = bookRepository.findByIsbn(bookCreateRequest.getIsbn()).orElse(null);

        if (findBook != null && !findBook.isDeleted()) {
            throw new BookException(ErrorCode.DUPLICATE_BOOK, Map.of("isbn", bookCreateRequest.getIsbn()), HttpStatus.CONFLICT);
        }

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
        if (findBook.isDeleted()) {
            throw new BookException(
                    ErrorCode.NO_ID_VARIABLE,
                    Map.of("bookId", bookId),
                    HttpStatus.NOT_FOUND
            );
        }

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
                return findBook.getThumbnailUrl();
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

    @Transactional(readOnly = true)
    public CursorPageResponseBookDto findBooks(String keyword, String orderBy,
                                               String direction, String cursor, Instant nextAfter, int limit) {
        long totalElements =
                keyword == null ? bookRepository.countByDeletedFalse() : bookRepository.countNotDeletedBooksByKeyword(keyword);

        Slice<Book> bookSlice = bookRepository.findBooks(keyword, orderBy, direction, cursor, nextAfter, limit);
        Slice<BookDto> bookDtoSlice = bookSlice.map(bookMapper::toDto);

        CursorPageResponseBookDto cursorPageResponseBookDto = CursorPageResponseBookDto.builder()
                .size(bookDtoSlice.getContent().size())
                .hasNext(bookDtoSlice.hasNext())
                .content(bookDtoSlice.getContent())
                .totalElements(totalElements)
                .nextCursor(getNextCursor(bookDtoSlice, orderBy))
                .nextAfter(getNextAfter(bookDtoSlice))
                .build();

        return cursorPageResponseBookDto;
    }

    @Transactional(readOnly = true)
    public BookDto findBookById(UUID bookId) {
        Book findBook = bookRepository.findById(bookId).orElse(null);
        if (findBook == null || findBook.isDeleted()) {
            throw new BookException(ErrorCode.NO_ID_VARIABLE,
                    Map.of("bookId", bookId), HttpStatus.NOT_FOUND);
        }

        return bookMapper.toDto(findBook);
    }

    public CursorPageResponsePopularBookDto getPopularBooks(PeriodType period, String cursor, Instant after, Sort.Direction direction, Integer limit) {

        List<Dashboard> bookDashboard = dashBoardRepository.findPopularBookListByCursor(RankingType.BOOK, period, cursor, after, direction, limit);

        List<PopularBookDto> popularBookDtoList = new ArrayList<>();

        bookDashboard.forEach(dashboard -> {
            Book book = bookRepository.findById(dashboard.getEntityId())
                    .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + dashboard.getEntityId()));
            popularBookDtoList.add(
                    bookMapper.toDto(dashboard, book, period)
            );
        });

        return bookCursorMapper.toCursorBookDto(popularBookDtoList, limit);
    }

    public void deleteSoft(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + bookId));

//        book.getReviews().forEach(review -> {
//            review.deleted();
//            review.getComments().forEach(Comment::softDelete);
//        });

        book.setDeletedAsTrue();
        bookRepository.save(book);
        log.info("도서 논리 삭제 완료: {}", bookId);
    }

    public void deleteHard(UUID bookId) {
        bookRepository.deleteById(bookId);
        log.info("도서 물리 삭제 완료: {}", bookId);
    }

    private String getNextCursor(Slice<BookDto> bookDtoSlice, String orderBy) {
        if (bookDtoSlice.getContent().isEmpty()) {
            return null;
        }
        List<BookDto> bookDtos = bookDtoSlice.getContent();
        BookDto bookDto = bookDtos.get(bookDtos.size() - 1);
        return switch (orderBy) {
            case "publishedDate" -> bookDto.getPublishedDate().toString();
            case "rating" -> Double.toString(bookDto.getRating());
            case "reviewCount" -> Integer.toString(bookDto.getReviewCount());
            default -> bookDto.getTitle();
        };
    }

    private Instant getNextAfter(Slice<BookDto> bookDtoSlice) {
        if (bookDtoSlice.getContent().isEmpty()) {
            return null;
        }
        List<BookDto> bookDtos = bookDtoSlice.getContent();
        return bookDtos.get(bookDtos.size() - 1).getCreatedAt();
    }
}

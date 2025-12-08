package com.codeit.sb06deokhugamteam2.book.service;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.data.PopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookImageCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponseBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.entity.BookStats;
import com.codeit.sb06deokhugamteam2.book.mapper.BookCursorMapper;
import com.codeit.sb06deokhugamteam2.book.mapper.BookMapper;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BookException;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.repository.DashboardRepository;
import com.codeit.sb06deokhugamteam2.review.adapter.out.entity.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final DashboardRepository dashboardRepository;
    private final S3Storage s3Storage;
    private final BookMapper bookMapper;
    private final BookCursorMapper bookCursorMapper;
    private final NaverSearchClient naverSearchClient;
    private final OcrService ocrService;

    public BookDto create(BookCreateRequest bookCreateRequest, Optional<BookImageCreateRequest> optionalBookImageCreateRequest) {
        if (bookRepository.findByIsbn(bookCreateRequest.getIsbn()).isPresent()) {
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

        BookStats bookStats = new BookStats();
        bookStats.setBook(book);
        book.setBookStats(bookStats);
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

    /*
    수정 작업은 재시도 하지 않음. 사용자가 확인 후 다시 수정 요청하도록 함.
    트랜잭션 커밋 시 버전 충돌나면 낙관적 락 예외 throw
     */
    public BookDto update(UUID bookId, BookUpdateRequest bookUpdateRequest, Optional<BookImageCreateRequest> optionalBookImageCreateRequest) {
        Book findBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(
                        ErrorCode.NO_ID_VARIABLE,
                        Map.of("bookId", bookId),
                        HttpStatus.NOT_FOUND));

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
                keyword == null ? bookRepository.count() : bookRepository.countBooksByKeyword(keyword);

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

    public String getIsbnByOcrApi(MultipartFile image) {
        return ocrService.getIsbnByOcrApi(image);
    }

    @Transactional(readOnly = true)
    public BookDto findBookById(UUID bookId) {
        Optional<Book> findBookOptional = bookRepository.findById(bookId);
        if (findBookOptional.isEmpty()) {
            throw new BookException(ErrorCode.NO_ID_VARIABLE,
                    Map.of("bookId", bookId), HttpStatus.NOT_FOUND);
        }

        Book findBook = findBookOptional.get();

        return bookMapper.toDto(findBook);
    }

    public CursorPageResponsePopularBookDto getPopularBooks(PeriodType period, String cursor, Instant after, Sort.Direction direction, Integer limit) {

        List<Dashboard> bookDashboard = dashboardRepository.findPopularBookListByCursor(RankingType.BOOK, period, cursor, after, direction, limit);

        List<PopularBookDto> popularBookDtoList = new ArrayList<>();

        LocalDateTime since = null;

        switch (period) {
            case DAILY -> since = LocalDate.now().atStartOfDay().minusDays(1);
            case WEEKLY -> since = LocalDate.now().atStartOfDay().minusDays(7);
            case MONTHLY -> since = LocalDate.now().atStartOfDay().minusMonths(1);
            case ALL_TIME -> since = LocalDateTime.MIN;
        }

        LocalDateTime finalSince = since;

        bookDashboard.forEach(dashboard -> {
            Book book = bookRepository.findById(dashboard.getEntityId())
                    .orElseThrow(() -> new BookException(
                            ErrorCode.NO_ID_VARIABLE,
                            Map.of("bookId", dashboard.getEntityId()),
                            HttpStatus.NOT_FOUND)
                    );

            List<Review> periodReviews = book.getReviews().stream().filter(review ->
                    // 같거나 이후
                    !review.createdAt().isBefore(finalSince.toInstant(ZoneOffset.UTC))
            ).toList();

            long reviewCount = periodReviews.size();

            if(reviewCount==0){
                return;
            }

            double rating = periodReviews.stream()
                    .mapToDouble(Review::rating)
                    .sum() / reviewCount;

            popularBookDtoList.add(
                    bookMapper.toDto(dashboard, book, period, reviewCount, rating)
            );
        });

        return bookCursorMapper.toCursorBookDto(popularBookDtoList, limit);
    }

    // 마지막 재시도 후 실패 시 낙관적 락 예외 그대로 throw
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 100,
            backoff = @Backoff(delay = 100)
    )
    public void deleteSoft(UUID bookId) {
        log.info("도서 논리 삭제 시도: {}", bookId);
        bookRepository.deleteSoft_Reviews_CommentsByBookId(bookId);
        bookRepository.deleteSoft_ReviewsByBookId(bookId);
        bookRepository.deleteSoftById(bookId);
        log.info("도서 논리 삭제 완료: {}", bookId);
    }

    public void deleteHard(UUID bookId) {
        bookRepository.deleteHardById(bookId);
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

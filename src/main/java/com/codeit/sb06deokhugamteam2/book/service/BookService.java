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
import com.codeit.sb06deokhugamteam2.book.mapper.BookCursorMapper;
import com.codeit.sb06deokhugamteam2.book.mapper.BookMapper;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.BookException;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.OcrException;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.repository.DashboardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final ObjectMapper objectMapper;
    private final NaverSearchClient naverSearchClient;

    @Value("${spring.ocr.api-key}")
    private String ocrApiKey;

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
        Optional<Book> findBookOptional = bookRepository.findById(bookId);

        if (findBookOptional.isEmpty()) {
            throw new BookException(
                    ErrorCode.NO_ID_VARIABLE,
                    Map.of("bookId", bookId),
                    HttpStatus.NOT_FOUND
            );
        }

        Book findBook = findBookOptional.get();

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

        List<Dashboard> bookDashboard = dashBoardRepository.findPopularBookListByCursor(RankingType.BOOK, period, cursor, after, direction, limit);

        List<PopularBookDto> popularBookDtoList = new ArrayList<>();

        bookDashboard.forEach(dashboard -> {
            Book book = bookRepository.findById(dashboard.getEntityId())
                    .orElseThrow(() -> new BookException(
                            ErrorCode.NO_ID_VARIABLE,
                            Map.of("bookId", dashboard.getEntityId()),
                            HttpStatus.NOT_FOUND)
                    );
            popularBookDtoList.add(
                    bookMapper.toDto(dashboard, book, period)
            );
        });

        return bookCursorMapper.toCursorBookDto(popularBookDtoList, limit);
    }

    @Transactional(readOnly = true)
    public String getIsbnByOcrApi(MultipartFile image) {

        // 무료버전 OCR API는 1MB 이하의 파일만 처리 가능
        // 월 25,000번 요청 가능
        if (image.getSize() > 1024 * 1024) {
            throw new RuntimeException("파일 크기는 1MB 이하여야 합니다.");
        }

        try {

            String json = callOcrApi(image);

            JsonNode root = objectMapper.readTree(json);

            String parsedText = root
                    .get("ParsedResults")
                    .get(0)
                    .get("ParsedText")
                    .asText();

            Pattern pattern = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");     // ex. 978-3-16-148410-0
            Matcher matcher = pattern.matcher(parsedText);

            if (matcher.find()) {
                return matcher.group(0).replaceAll("-", "");
            } else {
                throw new OcrException(ErrorCode.ISBN_NOT_FOUND,
                        Map.of("message", ErrorCode.ISBN_NOT_FOUND.getMessage(), "detail", parsedText),
                        HttpStatus.NOT_FOUND);
            }

        } catch (IOException e) {
            throw new OcrException(ErrorCode.OCR_API_ERROR,
                    Map.of("message", ErrorCode.OCR_API_ERROR.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteSoft(UUID bookId) {
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

    private String callOcrApi(MultipartFile image) throws IOException {

        final String url = "https://api.ocr.space/parse/image";
        final String language = "eng";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apikey", ocrApiKey)
                .addFormDataPart("language", language)
                .addFormDataPart("file", image.getOriginalFilename(),
                        RequestBody.create(
                                image.getBytes(),
                                MediaType.parse(image.getContentType())
                        ))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new OcrException(ErrorCode.OCR_API_ERROR,
                    Map.of("message", ErrorCode.OCR_API_ERROR.getMessage()),
                    HttpStatus.BAD_GATEWAY);
        }
    }
}

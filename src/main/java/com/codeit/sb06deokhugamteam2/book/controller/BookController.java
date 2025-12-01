package com.codeit.sb06deokhugamteam2.book.controller;

import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookImageCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.service.BookService;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> create(
            @RequestPart(value = "bookData") @Valid BookCreateRequest bookCreateRequest,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile imageData
    ) {
        Optional<BookImageCreateRequest> bookImageCreateRequest =
                Optional.ofNullable(imageData).flatMap(this::resolveBookImageCreateRequest);
        BookDto bookDto = bookService.create(bookCreateRequest, bookImageCreateRequest);

        return ResponseEntity.ok(bookDto);
    }

    @GetMapping("/info")
    public ResponseEntity<NaverBookDto> info(@RequestParam(value = "isbn") String isbn) {
        NaverBookDto naverBookDto = bookService.info(isbn);
        return ResponseEntity.ok(naverBookDto);
    }

    @PatchMapping(value = "/{bookId}", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> update(
            @PathVariable(value = "bookId") UUID bookId,
            @RequestPart(value = "bookData") @Valid BookUpdateRequest bookUpdateRequest,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile imageData) {
        Optional<BookImageCreateRequest> bookImageCreateRequest =
                Optional.ofNullable(imageData).flatMap(this::resolveBookImageCreateRequest);
        BookDto bookDto = bookService.update(bookId, bookUpdateRequest, bookImageCreateRequest);
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping("/popular")
    public ResponseEntity<CursorPageResponsePopularBookDto> getPopularBookList(
            @RequestParam(defaultValue = "DAILY") PeriodType period,        // enum에 해당하는 값이 없으면 400 에러, MethodArgumentTypeMismatchException 발생
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String cursor,        // 랭크 기준 커서
            @RequestParam(required = false) Instant after,        // 보조커서
            @RequestParam(defaultValue = "50") Integer limit
    ) {
        CursorPageResponsePopularBookDto response = bookService.getPopularBooks(period, cursor, after, direction, limit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteSoft(
            @PathVariable UUID bookId
    ) {
        bookService.deleteSoft(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{bookId}/hard")
    public ResponseEntity<Void> deleteHard(
            @PathVariable UUID bookId
    ) {
        bookService.deleteHard(bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Optional<BookImageCreateRequest> resolveBookImageCreateRequest(MultipartFile imageData) {
        if (imageData == null || imageData.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BookImageCreateRequest bookImageCreateRequest = new BookImageCreateRequest(
                        imageData.getBytes(),
                        imageData.getContentType(),
                        imageData.getOriginalFilename()
                );

                return Optional.of(bookImageCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

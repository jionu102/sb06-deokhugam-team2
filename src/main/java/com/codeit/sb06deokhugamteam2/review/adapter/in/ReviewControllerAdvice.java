package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorResponse;
import com.codeit.sb06deokhugamteam2.review.domain.exception.DuplicateReviewException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewBookNotFoundException;
import com.codeit.sb06deokhugamteam2.review.domain.exception.ReviewUserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;

@RestControllerAdvice
public class ReviewControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ReviewControllerAdvice.class);

    @ExceptionHandler(ReviewUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(ReviewUserNotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("USER_NOT_FOUND")
                .message("사용자를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ReviewBookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(ReviewBookNotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("BOOK_NOT_FOUND")
                .message("도서를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReviewException(DuplicateReviewException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_ALREADY_EXISTS")
                .message("이미 작성한 리뷰가 있습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}

package com.codeit.sb06deokhugamteam2.review.adapter.in;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorResponse;
import com.codeit.sb06deokhugamteam2.review.domain.exception.*;
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
                .status(HttpStatus.BAD_REQUEST.value())
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
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AlreadyExistsReviewException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsReviewException(AlreadyExistsReviewException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_ALREADY_EXISTS")
                .message("이미 작성한 리뷰가 있습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFoundException(ReviewNotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_NOT_FOUND")
                .message("리뷰를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ReviewPermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handleReviewPermissionDeniedException(ReviewPermissionDeniedException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_NOT_OWNED")
                .message("본인이 작성한 리뷰만 수정/삭제할 수 있습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.FORBIDDEN.value())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(InvalidReviewRatingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReviewRatingException(InvalidReviewRatingException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("INVALID_REVIEW_RATING")
                .message("리뷰 평점은 1에서 5 사이의 값이어야 합니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(InvalidReviewContentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReviewContentException(InvalidReviewContentException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("INVALID_REVIEW_CONTENT")
                .message("리뷰 내용은 공백일 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> handleReviewException(ReviewException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_ERROR")
                .message(e.getMessage())
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

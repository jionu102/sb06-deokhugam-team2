package com.codeit.sb06deokhugamteam2.review.adapter.in.web;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorResponse;
import com.codeit.sb06deokhugamteam2.review.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.Collections;

@ControllerAdvice
public class ReviewControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ReviewControllerAdvice.class);

    @ExceptionHandler(InvalidReviewCountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidReviewCountException(InvalidReviewCountException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("INVALID_REVIEW_COUNT")
                .message("리뷰 카운트는 음수가 될 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
    
    @ExceptionHandler(ReviewBookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFoundException(ReviewBookNotFoundException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("BOOK_NOT_FOUND")
                .message("도서를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(ReviewUserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(ReviewUserNotFoundException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("USER_NOT_FOUND")
                .message("사용자를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(AlreadyExistsReviewException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsReviewException(AlreadyExistsReviewException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_ALREADY_EXISTS")
                .message("이미 작성한 리뷰가 있습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.CONFLICT.value())
                .build();
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReviewNotFoundException(ReviewNotFoundException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_NOT_FOUND")
                .message("리뷰를 찾을 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(ReviewPermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleReviewPermissionDeniedException(ReviewPermissionDeniedException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_NOT_OWNED")
                .message("본인이 작성한 리뷰만 수정/삭제할 수 있습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.FORBIDDEN.value())
                .build();
    }
    
    @ExceptionHandler(InvalidReviewRatingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidReviewRatingException(InvalidReviewRatingException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("INVALID_REVIEW_RATING")
                .message("리뷰 평점은 1에서 5 사이의 값이어야 합니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
    
    @ExceptionHandler(InvalidReviewContentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidReviewContentException(InvalidReviewContentException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("INVALID_REVIEW_CONTENT")
                .message("리뷰 내용은 공백일 수 없습니다.")
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(ReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleReviewException(ReviewException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code("REVIEW_ERROR")
                .message(e.getMessage())
                .details(Collections.emptyMap())
                .exceptionType(e.getClass().getSuperclass().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}

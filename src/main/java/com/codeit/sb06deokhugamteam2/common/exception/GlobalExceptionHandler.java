package com.codeit.sb06deokhugamteam2.common.exception;

import com.codeit.sb06deokhugamteam2.common.exception.exceptions.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//<editor-fold desc="커스텀 예외처리 부분들">

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleUserExceptionHandler(UserException ex) {
    ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(MDCException.class)
  public ResponseEntity<ErrorResponse> handleMDCExceptionHandler(MDCException ex) {
    ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(NotificationException.class)
  public ResponseEntity<ErrorResponse> handleNotificationExceptionHandler(NotificationException ex) {
    ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(BookException.class)
  public ResponseEntity<ErrorResponse> handleBookException(BookException ex) {
      ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
      return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(OcrException.class)
  public ResponseEntity<ErrorResponse> handleOcrException(OcrException ex) {
    log.error("Ocr 에러", ex);
    ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
    return ResponseEntity.status(error.getStatus()).body(error);
  }
// </editor-fold>

  // <editor-fold desc="공통 예외처리 부분들">

  @ExceptionHandler(AWSException.class)
  public ResponseEntity<ErrorResponse> handleAWSException(AWSException ex) {
      ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
      return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(NaverSearchException.class)
  public ResponseEntity<ErrorResponse> handleNaverSearchException(NaverSearchException ex) {
      ErrorResponse error = createErrorResponse(ex, ex.getHttpStatus(), ex.getDetails());
      return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      ConstraintViolationException ex) {

    Map<String, Object> errorDetails = ((ConstraintViolationException) ex)
        .getConstraintViolations()
        .stream()
        .collect(Collectors.toUnmodifiableMap(
            violation -> {
              // 어떤 파라미터/속성에서 오류가 났는지
              // ex: "createUser.name" → 마지막 점(.) 이후만 사용하면 "name"
              String path = violation.getPropertyPath().toString();
              int lastDot = path.lastIndexOf('.');
              return lastDot != -1 ? path.substring(lastDot + 1) : path;
            },
            ConstraintViolation::getMessage,
            (existing, replacement) -> existing + ", " + replacement // 충돌 처리
        ));

    ErrorResponse error = createErrorResponse(ex, HttpStatus.BAD_REQUEST, errorDetails);
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MissingServletRequestParameterException ex) {
    ErrorResponse error = createErrorResponse(ex, HttpStatus.BAD_REQUEST, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 400 잘못된 요청
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {

    Map<String, Object> errorDetails = ((MethodArgumentNotValidException) ex)
        .getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(
            Collectors.toUnmodifiableMap(FieldError::getField, FieldError::getDefaultMessage
                , (existing, replacement) -> existing + ", " + replacement));


    ErrorResponse error = createErrorResponse(ex, HttpStatus.BAD_REQUEST, errorDetails);
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 400 잘못된 타입 (예: UUID 형식 오류)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    ErrorResponse error = createErrorResponse(ex, HttpStatus.BAD_REQUEST, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 404 리소스를 찾을 수 없음
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
    ErrorResponse error = createErrorResponse(ex, HttpStatus.NOT_FOUND, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 405 지원하지 않는 메소드
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex) {
    ErrorResponse error = createErrorResponse(ex, HttpStatus.METHOD_NOT_ALLOWED, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 409 중복 등 충돌 오류
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    ErrorResponse error = createErrorResponse(ex, HttpStatus.CONFLICT, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 409 낙관적 락 오류
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
      ObjectOptimisticLockingFailureException ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse error = createErrorResponse(ex, HttpStatus.CONFLICT, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }

  // 500 서버 내부 오류 (예상치 못한 모든 오류 처리)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse error = createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, Map.of());
    return ResponseEntity.status(error.getStatus()).body(error);
  }
  // </editor-fold>

  // <editor-fold desc="에러 양식 생성하는 부분 (클라이언트에 리턴하는 양식)">
  private ErrorResponse createErrorResponse(Exception ex, HttpStatus status, Map<String, Object> errorDetails) {

    Instant timeStamp = Instant.now();
    ErrorCode errorCode = ErrorCode.COMMON_EXCEPTION;

    try {
      ErrorResponse error = ErrorResponse.builder()
          .timestamp(timeStamp)
          .message(errorCode.getMessage())
          .code(errorCode.toString())
          .status(status.value())
          .exceptionType(ex.getClass().getName())
          .details(errorDetails).build();
      return error;
    } catch (Exception e) {
      log.error("ErrorResponse 생성 실패. exception 정보 : " + ex + " 오류 메세지 : ", e.getMessage());
      return null;
    }
  }
  // </editor-fold>
}

package com.codeit.sb06deokhugamteam2.common.exception.exceptions;


import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BasicException extends RuntimeException {
  protected final ErrorCode errorCode;
  protected final Map<String, Object> details;
  protected final HttpStatus httpStatus;

    public BasicException(ErrorCode errorCode, Map<String, Object> details, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.httpStatus = httpStatus;
    }
}

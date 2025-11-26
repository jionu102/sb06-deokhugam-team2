package com.codeit.sb06deokhugamteam2.common.exception.exceptions;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MDCException extends BasicException{
  public MDCException(ErrorCode errorCode, Map<String, Object> details, HttpStatus httpStatus) {
    super(errorCode, details, httpStatus);
  }
}

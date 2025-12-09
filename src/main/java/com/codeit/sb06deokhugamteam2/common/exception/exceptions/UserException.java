package com.codeit.sb06deokhugamteam2.common.exception.exceptions;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class UserException extends BasicException{

  public UserException(ErrorCode errorCode,
      Map<String, Object> details, HttpStatus httpStatus) {
    super(errorCode, details, httpStatus);
  }
}

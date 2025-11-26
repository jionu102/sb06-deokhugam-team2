package com.codeit.sb06deokhugamteam2.common.exception.exceptions;


import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BasicException extends RuntimeException {
  protected final ErrorCode errorCode;
  protected final Map<String, Object> details;
  protected final HttpStatus httpStatus;
}

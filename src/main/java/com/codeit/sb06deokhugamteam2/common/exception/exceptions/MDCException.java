package com.codeit.sb06deokhugamteam2.common.exception.exceptions;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MDCException extends RuntimeException{
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  @Override
  public String toString() {
    return "MDCException{" +
        ", errorCode = " + errorCode +
        ", details=" + details +
        '}';
  }

}

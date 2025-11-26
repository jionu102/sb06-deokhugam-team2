package com.codeit.sb06deokhugamteam2.common.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private Instant timestamp;
  private String code;
  private String message;
  private Map<String, Object> details;
  //발생한 예외클래스의 이름
  private String exceptionType;
  private int status;
}

package com.codeit.sb06deokhugamteam2.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  INVALID_USER_DATA("입력한 사용자 정보가 잘못 되었습니다"),
  INVALID_USER_PASSWORD("비밀번호가 맞지 않습니다."),
  NO_PATH_VARIABLE("Path 값이 없습니다"),
  NO_ID_VARIABLE("ID 값이 없습니다."),
  USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
  DUPLICATE_USER("이미 있는 사용자 입니다"),
  COMMON_EXCEPTION("오류가 발생 하였습니다."),
  EMPTY_DATA("데이터가 비어 있습니다."),
  INVALID_DATA("해당 데이터가 없습니다."),
  AWS_EXCEPTION("AWS 작업 도중 오류가 발생했습니다.");

  private String message;

  ErrorCode(String message) {
    this.message = message;
  }

}

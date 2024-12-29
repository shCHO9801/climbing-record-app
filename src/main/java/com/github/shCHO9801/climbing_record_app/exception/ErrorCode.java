package com.github.shCHO9801.climbing_record_app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 ID 입니다."),

  // JWT 관련 에러 코드 추가
  INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
  EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
  JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 토큰 생성에 실패했습니다.");


  private final HttpStatus httpStatus;
  private final String detail;
}

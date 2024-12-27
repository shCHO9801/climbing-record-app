package com.github.shCHO9801.climbing_record_app.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getDetail());
    this.errorCode = errorCode;
  }
}

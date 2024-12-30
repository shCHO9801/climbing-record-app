package com.github.shCHO9801.climbing_record_app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> customRequestException(final CustomException c) {
    ErrorCode errorCode = c.getErrorCode();
    logger.error("[CustomException]: {} - {}", errorCode.getHttpStatus(), errorCode.getDetail());
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(new ExceptionResponse(
            errorCode.getHttpStatus().value(),
            errorCode.getDetail()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
    logger.error("[Unhandled Exception] {}", ex.getMessage());
    return ResponseEntity
        .status(500)
        .body(new ExceptionResponse(
            500,
            "서버 내부 오류가 발생했습니다."
        ));
  }

  @Getter
  @AllArgsConstructor
  public static class ExceptionResponse {
    private int status;
    private String message;
  }
}

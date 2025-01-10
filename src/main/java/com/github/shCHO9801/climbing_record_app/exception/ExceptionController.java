package com.github.shCHO9801.climbing_record_app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingRequestHeaderException;
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

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ExceptionResponse> handleMissingRequestHeaderException(
      MissingRequestHeaderException ex) {
    String headerName = ex.getHeaderName();
    String message = String.format("필수 헤더 '%s'가 누락되었습니다.", headerName);
    ExceptionResponse errorResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
        "JWT 토큰이 존재하지 않거나 형식이 잘못되었습니다.");
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

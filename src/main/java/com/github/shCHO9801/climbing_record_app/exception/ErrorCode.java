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
  JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 토큰 생성에 실패했습니다."),

  JSON_CONVERT_ERROR(HttpStatus.BAD_REQUEST, "JSON 변환 오류입니다."),
  CLIMBING_GYM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 클라이밍장 입니다."),
  CLIMBING_GYM_NOT_FOUND(HttpStatus.BAD_REQUEST, "클라이밍장을 찾을 수 없습니다."),
  USER_MONTHLY_STATS_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 유저의 기록이 없습니다."),
  INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT 토큰이 존재하지 않거나 형식이 잘못되었습니다."),
  POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "Post를 찾을 수 없습니다."),
  UNAUTHORIZED_ACTION(HttpStatus.BAD_REQUEST, "작성자만 게시글을 수정, 삭제할 수 있습니다."),
  COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Comment를 찾을 수 없습니다."),
  MEETING_TIME_NULL(HttpStatus.BAD_REQUEST, "시작 시간과 종료 시간은 null일 수 없습니다."),
  MEETING_TIME_INVALID(HttpStatus.BAD_REQUEST, "시작 시간은 종료 시간보다 이전이어야 합니다."),
  MEETING_NOT_FOUND(HttpStatus.BAD_REQUEST, "Meeting을 찾을 수 없습니다."),
  MEETING_PARTICIPATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Meeting Participation을 찾을 수 없습니다."),
  MEETING_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "미팅 정원이 초과하였습니다."),
  MEETING_CAPACITY_INVALID(HttpStatus.BAD_REQUEST, "현재 참여자가 미팅 최대인원 수 보다 많습니다.");



  private final HttpStatus httpStatus;
  private final String detail;
}

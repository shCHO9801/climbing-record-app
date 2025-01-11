package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionRequest;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.service.ClimbingSessionService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/climbing-session")
@RequiredArgsConstructor
public class ClimbingSessionController {

  private final JwtTokenProvider provider;
  private final ClimbingSessionService climbingSessionService;

  @PostMapping
  public ResponseEntity<CreateSessionResponse> createClimbingSession(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody CreateSessionRequest request
  ) {
    String userId = extractUserId(authorizationHeader);
    return new ResponseEntity<>(
        climbingSessionService.createClimbingSession(userId, request),
        CREATED);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<CreateSessionResponse>> getAllClimbingSessions(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    String userId = extractUserId(authorizationHeader);
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(climbingSessionService.getAllClimbingSessions(userId, pageable));
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }

}

package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionRequest;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.service.ClimbingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/climbing-session")
@RequiredArgsConstructor
public class ClimbingSessionController {

  private final ClimbingSessionService climbingSessionService;

  @PostMapping
  public ResponseEntity<CreateSessionResponse> createClimbingSession(
      @RequestBody CreateSessionRequest request
  ) {
    return new ResponseEntity<>(
        climbingSessionService.createClimbingSession(request),
        CREATED);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<CreateSessionResponse>> getAllClimbingSessions(
      @RequestParam Long userNum,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(climbingSessionService.getAllClimbingSessions(userNum, pageable));
  }

}

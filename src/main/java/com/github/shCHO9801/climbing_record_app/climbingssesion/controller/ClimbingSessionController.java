package com.github.shCHO9801.climbing_record_app.climbingssesion.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession.Request;
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession.Response;
import com.github.shCHO9801.climbing_record_app.climbingssesion.service.ClimbingSessionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<Response> createClimbingSession(
      @RequestBody Request request
  ) {
    return new ResponseEntity<>(
        climbingSessionService.createClimbingSession(request),
        CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Response>> getAllClimbingSessions(
      @RequestParam Long userNum
  ) {
    return ResponseEntity.ok(climbingSessionService.getAllClimbingSessions(userNum));
  }

}

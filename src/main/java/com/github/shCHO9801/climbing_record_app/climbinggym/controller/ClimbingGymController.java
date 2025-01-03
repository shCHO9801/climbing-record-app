package com.github.shCHO9801.climbing_record_app.climbinggym.controller;

import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.GetGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.service.ClimbingGymService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class ClimbingGymController {

  private final ClimbingGymService climbingGymService;

  @PostMapping
  public ResponseEntity<CreateGym.Response> createClimbingGym(
      @Valid @RequestBody CreateGym.Request request) {
    CreateGym.Response createdGym = climbingGymService.createClimbingGym(request);
    return ResponseEntity.ok(createdGym);
  }

  @GetMapping
  public ResponseEntity<List<GetGym>> getAllClimbingGyms() {
    return ResponseEntity.ok(climbingGymService.getAllGyms());
  }
}
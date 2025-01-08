package com.github.shCHO9801.climbing_record_app.climbinggym.controller;

import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGymRequest;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGymResponse;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.GetGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.service.ClimbingGymService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class ClimbingGymController {

  private final ClimbingGymService climbingGymService;

  @PostMapping
  public ResponseEntity<CreateGymResponse> createClimbingGym(
      @RequestBody @Valid CreateGymRequest request) {
    CreateGymResponse createdGym = climbingGymService.createClimbingGym(request);
    return ResponseEntity.ok(createdGym);
  }

  @GetMapping
  public ResponseEntity<Page<GetGym>> getAllClimbingGyms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<GetGym> gyms = climbingGymService.getAllGyms(pageable);
    return ResponseEntity.ok(gyms);
  }
}

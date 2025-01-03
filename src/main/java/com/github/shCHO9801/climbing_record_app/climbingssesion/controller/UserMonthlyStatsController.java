package com.github.shCHO9801.climbing_record_app.climbingssesion.controller;

import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingssesion.service.UserMonthlyStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-monthly-stats")
@RequiredArgsConstructor
public class UserMonthlyStatsController {
  private final UserMonthlyStatsService userMonthlyStatsService;

  @GetMapping
  public ResponseEntity<UserMonthlyStatsResponse> getUserMonthlyStats(
      @RequestParam Long userNum,
      @RequestParam int year,
      @RequestParam int month
  ) {
    UserMonthlyStatsResponse stats = userMonthlyStatsService.getUserMonthlyStats(
        userNum, year, month);
    return ResponseEntity.ok(stats);
  }
}

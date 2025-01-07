package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.service.UserMonthlyStatsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    LocalDate localDate = LocalDate.of(year, month, 1);
    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStats(userNum, localDate)
    );
  }

  @GetMapping("/paginated")
  public ResponseEntity<PagedResponse<UserMonthlyStatsResponse>> getUserMonthlyStatsPaginated(
      @RequestParam Long userNum,
      @RequestParam int page,
      @RequestParam int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStatsPaginated(userNum, pageable));

  }

  @GetMapping("/yearly")
  public ResponseEntity<PagedResponse<UserMonthlyStatsResponse>> getUserMonthlyStatsByYear(
      @RequestParam Long userNum,
      @RequestParam int year,
      @RequestParam int page,
      @RequestParam int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStatsByYear(userNum, year, pageable)
    );
  }
}

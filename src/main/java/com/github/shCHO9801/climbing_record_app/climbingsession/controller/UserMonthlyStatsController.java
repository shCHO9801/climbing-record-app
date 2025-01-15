package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.service.UserMonthlyStatsService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-monthly-stats")
@RequiredArgsConstructor
public class UserMonthlyStatsController {

  private final JwtTokenProvider provider;
  private final UserMonthlyStatsService userMonthlyStatsService;
  private final UserRepository userRepository;

  @GetMapping
  public ResponseEntity<UserMonthlyStatsResponse> getUserMonthlyStats(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam int year,
      @RequestParam int month
  ) {
    LocalDate localDate = LocalDate.of(year, month, 1);

    String userId = extractUserId(authorizationHeader);

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Long userNum = user.getUserNum();
    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStats(userNum, localDate)
    );
  }

  @GetMapping("/paginated")
  public ResponseEntity<PagedResponse<UserMonthlyStatsResponse>> getUserMonthlyStatsPaginated(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);

    String userId = extractUserId(authorizationHeader);

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Long userNum = user.getUserNum();

    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStatsPaginated(userNum, pageable));

  }

  @GetMapping("/yearly")
  public ResponseEntity<PagedResponse<UserMonthlyStatsResponse>> getUserMonthlyStatsByYear(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam int year,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    String userId = extractUserId(authorizationHeader);

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Long userNum = user.getUserNum();

    return ResponseEntity.ok(
        userMonthlyStatsService.getUserMonthlyStatsByYear(userNum, year, pageable)
    );
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }
}

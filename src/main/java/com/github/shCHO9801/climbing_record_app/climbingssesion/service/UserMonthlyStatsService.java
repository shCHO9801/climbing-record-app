package com.github.shCHO9801.climbing_record_app.climbingssesion.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_MONTHLY_STATS_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.UserMonthlyStats;
import com.github.shCHO9801.climbing_record_app.climbingssesion.repository.UserMonthlyStatsRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMonthlyStatsService {

  private final UserMonthlyStatsRepository userMonthlyStatsRepository;

  public UserMonthlyStatsResponse getUserMonthlyStats(
      Long userNum, int year, int month
  ) {
    YearMonth yearMonth = YearMonth.of(year, month);
    UserMonthlyStats stats = userMonthlyStatsRepository.findByUserNumAndYearMonth(userNum, yearMonth)
        .orElseThrow(() -> new CustomException(USER_MONTHLY_STATS_NOT_FOUND));

    return UserMonthlyStatsResponse.builder()
        .userNum(stats.getUserNum())
        .yearMonth(stats.getYearMonth())
        .totalDuration(stats.getTotalDuration())
        .build();
  }

  public void aggregateUserMonthlyStats(Long userNum, YearMonth yearMonth, int duration) {
    UserMonthlyStats stats = userMonthlyStatsRepository.findByUserNumAndYearMonth(userNum, yearMonth)
        .orElse(UserMonthlyStats.builder()
            .userNum(userNum)
            .yearMonth(yearMonth)
            .totalDuration(0)
            .build());

        stats.setTotalDuration(stats.getTotalDuration() + duration);
        userMonthlyStatsRepository.save(stats);
  }

}

package com.github.shCHO9801.climbing_record_app.climbingssesion.dto;

import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMonthlyStatsResponse {
  private Long userNum;
  private YearMonth yearMonth;
  private int totalDuration;
}

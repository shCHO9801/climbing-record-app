package com.github.shCHO9801.climbing_record_app.climbingsession.dto;

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
  private String yearMonth;
  private int totalDuration;
}

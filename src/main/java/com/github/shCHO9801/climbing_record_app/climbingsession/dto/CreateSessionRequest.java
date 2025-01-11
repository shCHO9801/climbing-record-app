package com.github.shCHO9801.climbing_record_app.climbingsession.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

  private LocalDate date;
  private int duration;
  private Long climbingGymId;
  private Map<String, Integer> difficultyLevels;
}

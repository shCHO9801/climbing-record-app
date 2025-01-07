package com.github.shCHO9801.climbing_record_app.climbingsession.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class CreateSession {

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    private LocalDate date;
    private int duration;
    private Long userId;
    private Long climbingGymId;
    private Map<String, Integer> difficultyLevels;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {

    private Long id;
    private LocalDate date;
    private int duration;
    private Map<String, Integer> difficultyLevels;
    private Long userId;
    private Long climbingGymId;
    private String climbingGymName;
  }
}

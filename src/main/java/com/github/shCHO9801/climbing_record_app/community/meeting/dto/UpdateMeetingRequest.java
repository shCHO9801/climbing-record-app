package com.github.shCHO9801.climbing_record_app.community.meeting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class UpdateMeetingRequest {

  private String title;
  private String description;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;

  @Min(1)
  @Max(20)
  private int capacity;
}

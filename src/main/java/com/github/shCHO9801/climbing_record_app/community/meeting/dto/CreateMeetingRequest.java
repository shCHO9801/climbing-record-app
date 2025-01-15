package com.github.shCHO9801.climbing_record_app.community.meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeetingRequest {

  private String title;
  private String description;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  @Min(1)
  private int capacity;
}

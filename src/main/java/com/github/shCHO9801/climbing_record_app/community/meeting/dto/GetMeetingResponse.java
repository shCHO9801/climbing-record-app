package com.github.shCHO9801.climbing_record_app.community.meeting.dto;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class GetMeetingResponse {

  private Long id;
  private String hostId;
  private String title;
  private String description;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  @Min(1)
  @Max(20)
  private int capacity;
  private int participantCount;
  private LocalDateTime createdAt;

  public static GetMeetingResponse from(Meeting meeting) {
    return GetMeetingResponse.builder()
        .id(meeting.getId())
        .hostId(meeting.getHost().getId())
        .title(meeting.getTitle())
        .description(meeting.getDescription())
        .date(meeting.getDate())
        .startTime(meeting.getStartTime())
        .endTime(meeting.getEndTime())
        .capacity(meeting.getCapacity())
        .participantCount(meeting.getParticipantCount())
        .createdAt(meeting.getCreatedAt())
        .build();
  }
}

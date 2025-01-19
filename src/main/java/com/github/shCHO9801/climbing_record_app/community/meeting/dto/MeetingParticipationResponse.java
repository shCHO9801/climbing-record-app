package com.github.shCHO9801.climbing_record_app.community.meeting.dto;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Status;
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
public class MeetingParticipationResponse {

  private Long id;
  private Long meetingId;
  private String userId;
  private Status status;

  public static MeetingParticipationResponse from(MeetingParticipation participation) {
    return MeetingParticipationResponse.builder()
        .id(participation.getId())
        .meetingId(participation.getMeeting().getId())
        .userId(participation.getUser().getId())
        .status(participation.getStatus())
        .build();
  }
}

package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingParticipationHelperService {

  private final MeetingRepository meetingRepository;
  private final MeetingParticipationRepository meetingParticipationRepository;

  public MeetingParticipationHelperService(MeetingRepository meetingRepository, MeetingParticipationRepository meetingParticipationRepository) {
    this.meetingRepository = meetingRepository;
    this.meetingParticipationRepository = meetingParticipationRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Meeting loadMeetingForUpdate(Long meetingId) {
    return meetingRepository.findById(meetingId)
        .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void incrementParticipantCount(Meeting meeting) {
    int updated = meetingRepository.increaseParticipantCount(meeting.getId());
    if(updated == 0) {
      throw new CustomException(MEETING_NOT_FOUND);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public MeetingParticipation saveParticipation(MeetingParticipation participation) {
    return meetingParticipationRepository.save(participation);
  }
}

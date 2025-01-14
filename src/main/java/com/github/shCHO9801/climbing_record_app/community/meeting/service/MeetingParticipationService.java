package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_CAPACITY_EXCEEDED;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_PARTICIPATION_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Status;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingParticipationService {

  private final MeetingRepository meetingRepository;
  private final MeetingParticipationRepository meetingParticipationRepository;
  private final UserRepository userRepository;

  @Transactional
  public MeetingParticipation participation(String userId, Long meetingId) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));

    if (meeting.getParticipantCount() >= meeting.getCapacity()) {
      throw new CustomException(MEETING_CAPACITY_EXCEEDED);
    }

    MeetingParticipation meetingParticipation = MeetingParticipation.create(meeting, user);
    MeetingParticipation savedParticipation = meetingParticipationRepository.save(
        meetingParticipation);

    meeting.setParticipantCount(meeting.getParticipantCount() + 1);
    meetingRepository.save(meeting);

    return savedParticipation;
  }

  public Page<MeetingParticipation> getAllParticipation(Long meetingId, Pageable pageable) {
    return meetingParticipationRepository.findAllByMeetingId(meetingId, pageable);
  }

  @Transactional
  public void cancelParticipation(String userId, Long participationId) {
    MeetingParticipation meetingParticipation = meetingParticipationRepository.findById(
            participationId)
        .orElseThrow(() -> new CustomException(MEETING_PARTICIPATION_NOT_FOUND));

    if (!userId.equals(meetingParticipation.getUser().getId())) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }

    meetingParticipation.setStatus(Status.CANCELLED);
    meetingParticipationRepository.save(meetingParticipation);

    Meeting meeting = meetingParticipation.getMeeting();
    meeting.setParticipantCount(meeting.getParticipantCount() - 1);
    meetingRepository.save(meeting);
  }

}

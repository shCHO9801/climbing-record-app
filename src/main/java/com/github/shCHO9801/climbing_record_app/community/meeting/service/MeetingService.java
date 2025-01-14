package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.meeting.controller.UpdateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final UserRepository userRepository;

  @Transactional
  public Meeting createMeeting(String userId, CreateMeetingRequest request) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Meeting.val

    Meeting meeting = Meeting.buildMeeting(user, request);
    return meetingRepository.save(meeting);
  }

  public Page<Meeting> getAllMeetings(Pageable pageable) {
    return meetingRepository.findAll(pageable);
  }

  public Meeting updateMeeting(String userId, Long meetingId, UpdateMeetingRequest request) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));

    if(!Objects.equals(userId, meeting.getHost().getId())) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }

    meeting.setTitle(request.getTitle() != null ? request.getTitle() : meeting.getTitle());
    meeting.setDescription(request.getDescription() != null ? request.getDescription() : meeting.getDescription());
    meeting.setDate(request.getDate() != null ? request.getDate() : meeting.getDate());
    meeting.setStartTime(request.getStartTime() != null ? request.getStartTime() : meeting.getStartTime());
    meeting.setEndTime(request.getEndTime() != null ? request.getEndTime() : meeting.getEndTime());
    meeting.setCapacity(request.getCapacity());

    return meetingRepository.save(meeting);
  }

  public void deleteMeeting(String userId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));

    if(!Objects.equals(userId, meeting.getHost().getId())) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }

    //TODO : 참여자 명단 삭제
    meetingRepository.deleteById(meetingId);
  }
}

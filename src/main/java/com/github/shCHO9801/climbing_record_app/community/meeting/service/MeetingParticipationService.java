package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_ALREADY_JOINED;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingParticipationService {

  private final MeetingRepository meetingRepository;
  private final MeetingParticipationRepository meetingParticipationRepository;
  private final UserRepository userRepository;
  private final MeetingParticipationHelperService helperService;
  private static final Logger logger = LoggerFactory.getLogger(MeetingParticipationService.class);


  @Retryable(
      value = { PessimisticLockingFailureException.class },
      maxAttempts = 5,
      backoff = @Backoff(delay = 200)
  )
  public MeetingParticipation participation(String userId, Long meetingId) {
    logger.info("참여 시작: userId={}, meetingId={}", userId, meetingId);

    // 체크: 이미 참여한 유저에 대한 로직은 여기서 간단하게 처리 가능 (생략 또는 별도 처리)
    Optional<MeetingParticipation> existingParticipation =
        meetingParticipationRepository.findByMeetingIdAndUser_Id(meetingId, userId);

    if (existingParticipation.isPresent()) {
      MeetingParticipation participation = existingParticipation.get();
      if (participation.getStatus() == Status.JOIN) {
        throw new CustomException(MEETING_ALREADY_JOINED);
      } else if (participation.getStatus() == Status.CANCELLED) {
        participation.setStatus(Status.JOIN);
        Meeting meeting = helperService.loadMeetingForUpdate(meetingId);
        if (meeting.getParticipantCount() >= meeting.getCapacity()) {
          throw new CustomException(MEETING_CAPACITY_EXCEEDED);
        }
        helperService.incrementParticipantCount(meeting);
        logger.info("재참여 처리 완료: userId={}, meetingId={}", userId, meetingId);
        return helperService.saveParticipation(participation);
      }
    }

    Meeting meeting = helperService.loadMeetingForUpdate(meetingId);
    if (meeting.getParticipantCount() >= meeting.getCapacity()) {
      throw new CustomException(MEETING_CAPACITY_EXCEEDED);
    }

    MeetingParticipation participation =
        MeetingParticipation.create(meeting, userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND)));

    MeetingParticipation savedParticipation = helperService.saveParticipation(participation);
    helperService.incrementParticipantCount(meeting);

    logger.info("참여 종료: userId={}, meetingId={}, newParticipantCount={}",
        userId, meetingId, meeting.getParticipantCount());

    return savedParticipation;
  }

  public Page<MeetingParticipation> getAllParticipation(Long meetingId, Pageable pageable) {
    return meetingParticipationRepository.findAllByMeetingIdAndStatusNot(meetingId,
        Status.CANCELLED, pageable);
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

package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_CAPACITY_EXCEEDED;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Status;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("소모임 참여 명단 유닛 테스트")
class MeetingParticipationServiceTest {

  @InjectMocks
  private MeetingParticipationService participationService;

  @Mock
  private MeetingRepository meetingRepository;

  @Mock
  private MeetingParticipationRepository participationRepository;

  @Mock
  private UserRepository userRepository;

  private User user;
  private Meeting meeting;
  private MeetingParticipation meetingParticipation;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .userNum(1L)
        .id("testUser")
        .password("password")
        .build();

    meeting = Meeting.builder()
        .id(100L)
        .title("testMeeting")
        .description("testDescription")
        .date(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .participantCount(0)
        .host(user)
        .build();

    meetingParticipation = MeetingParticipation.create(meeting, user);
    meetingParticipation.setId(100L);
  }

  @Test
  @DisplayName("참여 생성 성공")
  void participationSuccess() {
    //given
    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));
    when(participationRepository.save(any(MeetingParticipation.class)))
        .thenReturn(meetingParticipation);

    //when
    MeetingParticipation savedParticipation = participationService.participation(user.getId(),
        meeting.getId());

    //then
    assertNotNull(savedParticipation);
    assertEquals(100L, savedParticipation.getId());
    verify(meetingRepository, times(1)).save(meeting);
    assertEquals(1, meeting.getParticipantCount());
  }

  @Test
  @DisplayName("참여 생성 실패 - capacity 초과")
  void participationFailCapacityExceeded() {
    //given
    meeting.setParticipantCount(5);
    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> participationService.participation(user.getId(), meeting.getId()));
    assertEquals(MEETING_CAPACITY_EXCEEDED, exception.getErrorCode());
  }

  @Test
  @DisplayName("참여 생성 실패 - 유저 미존재")
  void participationFailUserNotFound() {
    //given
    when(userRepository.findByUsername("nonExistUser"))
        .thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> participationService.participation(user.getId(), meeting.getId()));
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("참여 생성 실패 - 소모임 미존재")
  void participationFailMeetingNotFound() {
    //given
    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(meetingRepository.findById(9999L))
        .thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> participationService.participation(user.getId(), meeting.getId()));
    assertEquals(MEETING_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("참여 취소 성공")
  void cancelParticipationSuccess() {
      //given
    meetingParticipation.setStatus(Status.JOIN);
    meeting.setParticipantCount(3);
    when(participationRepository.findById(meetingParticipation.getId()))
        .thenReturn(Optional.of(meetingParticipation));
    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(meetingRepository.save(any(Meeting.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

      //when
    participationService.cancelParticipation(user.getId(), meeting.getId());

      //then
    assertEquals(Status.CANCELLED, meetingParticipation.getStatus());
    assertEquals(2, meeting.getParticipantCount());
  }

  @Test
  @DisplayName("참여 취소 실패 - 권한 없음")
  void cancelParticipationFailUnauthorized() {
      //given
    User anotherUser = User.builder().userNum(2L).id("anotherUser").build();
    meetingParticipation.setUser(anotherUser);
    when(participationRepository.findById(meetingParticipation.getId()))
        .thenReturn(Optional.of(meetingParticipation));

      //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> participationService.cancelParticipation(user.getId(), meetingParticipation.getId()));
    assertEquals(UNAUTHORIZED_ACTION, exception.getErrorCode());
  }
}
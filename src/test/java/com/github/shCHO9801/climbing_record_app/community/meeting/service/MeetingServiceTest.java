package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_CAPACITY_INVALID;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.UpdateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("소모임 유닛 테스트")
class MeetingServiceTest {

  @InjectMocks
  private MeetingService meetingService;

  @Mock
  private MeetingRepository meetingRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MeetingParticipationRepository participationRepository;

  private User user;
  private Meeting meeting;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .userNum(1L)
        .id("testId")
        .password("testPassword")
        .role(USER)
        .build();

    meeting = Meeting.builder()
        .id(1L)
        .title("testTitle")
        .description("testDescription")
        .date(LocalDate.from(LocalDateTime.now().plusDays(1)))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .participantCount(0)
        .host(user)
        .build();
  }

  @Test
  @DisplayName("소모임 생성 성공")
  void createMeetingSuccess() {
    //given
    CreateMeetingRequest createMeetingRequest = CreateMeetingRequest.builder()
        .title("testTitle")
        .description("testDescription")
        .date(LocalDate.from(LocalDateTime.now().plusDays(1)))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .build();

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(meetingRepository.save(any(Meeting.class)))
        .thenReturn(meeting);

    //when
    Meeting created = meetingService.createMeeting(user.getId(), createMeetingRequest);

    //then
    assertNotNull(created);
    assertEquals(meeting.getId(), created.getId());
    assertEquals(meeting.getTitle(), created.getTitle());
    assertEquals(meeting.getCapacity(), created.getCapacity());
    verify(meetingRepository).save(any(Meeting.class));
  }

  @Test
  @DisplayName("소모임 생성 실패 - 유저 미존재")
  void createMeetingFailUserNotFound() {
    //given
    CreateMeetingRequest createMeetingRequest = CreateMeetingRequest.builder()
        .title("testTitle")
        .description("testDescription")
        .date(LocalDate.from(LocalDateTime.now().plusDays(1)))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .build();

    when(userRepository.findByUsername("nonExistsUser"))
        .thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> meetingService.createMeeting("nonExistsUser", createMeetingRequest));

    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("소모임 수정 성공")
  void updateMeetingSuccess() {
    //given
    meeting.setParticipantCount(2);
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));

    UpdateMeetingRequest updateRequest = UpdateMeetingRequest.builder()
        .title("updated Title")
        .capacity(5)
        .build();

    when(meetingRepository.save(any(Meeting.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    //when
    Meeting updated = meetingService.updateMeeting(user.getId(), meeting.getId(), updateRequest);

    //then
    assertNotNull(updated);
    assertEquals("updated Title", updated.getTitle());
    assertEquals(5, updated.getCapacity());
  }

  @Test
  @DisplayName("소모임 수정 실패 - capacity가 현재 참여자 수보다 작음.")
  void updateMeetingFailCapacityInvalid() {
    //given
    meeting.setParticipantCount(3);
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));

    UpdateMeetingRequest updateRequest = UpdateMeetingRequest.builder()
        .capacity(2)
        .build();

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> meetingService.updateMeeting(user.getId(), meeting.getId(), updateRequest));

    assertEquals(MEETING_CAPACITY_INVALID, exception.getErrorCode());
  }

  @Test
  @DisplayName("소모임 삭제 성공")
  void deleteMeetingSuccess() {
    //given
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));

    //when&then
    meetingService.deleteMeeting(user.getId(), meeting.getId());
    verify(meetingRepository, times(1)).deleteById(meeting.getId());
  }

  @Test
  @DisplayName("소모임 삭제 실패 - 권한 없음")
  void deleteMeetingFailUnauthorized() {
    //given
    when(meetingRepository.findById(meeting.getId()))
        .thenReturn(Optional.of(meeting));

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> meetingService.deleteMeeting("nonExistUser", meeting.getId()));
    assertEquals(UNAUTHORIZED_ACTION, exception.getErrorCode());

  }
}
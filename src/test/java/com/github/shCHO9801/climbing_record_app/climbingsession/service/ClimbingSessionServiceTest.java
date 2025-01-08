package com.github.shCHO9801.climbing_record_app.climbingsession.service;

import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionRequest;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.entity.ClimbingSession;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("운동 세션 유닛 테스트")
class ClimbingSessionServiceTest {

  @InjectMocks
  private ClimbingSessionService climbingSessionService;

  @Mock
  private ClimbingSessionRepository climbingSessionRepository;

  @Mock
  private ClimbingGymRepository climbingGymRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMonthlyStatsService userMonthlyStatsService;

  private ClimbingGym climbingGym;
  private User user;
  private ClimbingSession climbingSession;
  private ClimbingSession climbingSession1;
  private ClimbingSession climbingSession2;

  private static LocalDate date;
  private static String yearMonth;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    date = LocalDate.of(2025, 1, 1);
    yearMonth = String.format("%d-%02d", date.getYear(), date.getMonthValue());
    GeometryFactory geometryFactory = new GeometryFactory();
    climbingGym = ClimbingGym.builder()
        .id(1L)
        .name("더클라임 강남점")
        .location(geometryFactory.createPoint(new Coordinate(127.0, 37.0)))
        .difficultyChart(Arrays.asList("하양", "노랑", "주황", "초록", "파랑", "빨강", "보라", "회색", "갈색", "검정"))
        .build();

    user = User.builder()
        .userNum(100L)
        .id("user100")
        .password("password")
        .email("user100@example.com")
        .role(USER)
        .nickname("Tester")
        .height(175.0)
        .armLength(80.0)
        .equipmentInfo("{}")
        .build();

    climbingSession = ClimbingSession.builder()
        .id(1L)
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevelsCompleted(new HashMap<>())
        .user(user)
        .climbingGym(climbingGym)
        .build();

    climbingSession1 = ClimbingSession.builder()
        .id(1L)
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevelsCompleted(new HashMap<>())
        .user(user)
        .climbingGym(climbingGym)
        .build();

    climbingSession2 = ClimbingSession.builder()
        .id(2L)
        .date(LocalDate.of(2025, 1, 2))
        .duration(120)
        .difficultyLevelsCompleted(new HashMap<>())
        .user(user)
        .climbingGym(climbingGym)
        .build();
  }

  @Test
  @DisplayName("클라이밍 세션 생성 성공")
  public void createClimbingSessionSuccess() {
    // given
    CreateSessionRequest request = CreateSessionRequest.builder()
        .climbingGymId(1L)
        .userId(100L)
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevels(new HashMap<>())
        .build();

    when(climbingGymRepository.findById(1L)).thenReturn(Optional.of(climbingGym));
    when(userRepository.findByUserNum(100L)).thenReturn(Optional.of(user));
    when(climbingSessionRepository.save(any(ClimbingSession.class))).thenReturn(climbingSession);

    // when
    CreateSessionResponse response = climbingSessionService.createClimbingSession(request);

    // then
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(date, response.getDate());
    assertEquals(90, response.getDuration());
    assertEquals(100L, response.getUserId());
    assertEquals(1L, response.getClimbingGymId());
    assertEquals(climbingGym.getName(), response.getClimbingGymName());

    verify(userMonthlyStatsService, times(1)).aggregateUserMonthlyStats(100L, date,
        90);
  }

  @Test
  @DisplayName("클라이밍 세션 생성 실패 - 클라이밍장이 존재하지 않음")
  public void createClimbingSessionGymNotFound() {
    // given
    CreateSessionRequest request = CreateSessionRequest.builder()
        .climbingGymId(2L) // 존재하지 않는 클라이밍장 ID
        .userId(100L)
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevels(new HashMap<>())
        .build();

    when(climbingGymRepository.findById(2L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(CustomException.class, () -> {
      climbingSessionService.createClimbingSession(request);
    });

    verify(userMonthlyStatsService, never()).aggregateUserMonthlyStats(anyLong(), any(), anyInt());
  }

  @Test
  @DisplayName("클라이밍 세션 생성 실패 - 사용자가 존재하지 않음")
  public void createClimbingSessionUserNotFound() {
    // given
    CreateSessionRequest request = CreateSessionRequest.builder()
        .climbingGymId(1L)
        .userId(200L) // 존재하지 않는 사용자 ID
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevels(new HashMap<>())
        .build();

    when(climbingGymRepository.findById(1L)).thenReturn(Optional.of(climbingGym));
    when(userRepository.findByUserNum(200L)).thenReturn(Optional.empty());

    // when & then
    assertThrows(CustomException.class,
        () -> climbingSessionService.createClimbingSession(request));

    verify(userMonthlyStatsService, never()).aggregateUserMonthlyStats(anyLong(), any(), anyInt());
  }

  @Test
  @DisplayName("모든 클라이밍 세션 조회 성공")
  public void getAllClimbingSessionsSuccess() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    List<ClimbingSession> sessions = Arrays.asList(climbingSession1, climbingSession2);
    when(climbingSessionRepository.findByUser_UserNum(100L, pageable)).thenReturn(
        new PageImpl<>(sessions));

    // when
    PagedResponse<CreateSessionResponse> responses = climbingSessionService.getAllClimbingSessions(
        100L,
        pageable);

    // then
    assertNotNull(responses);
    assertEquals(2, responses.getTotalElements());

  }

  @Test
  @DisplayName("모든 클라이밍 세션 조회 성공 - 빈 리스트")
  public void getAllClimbingSessionsEmpty() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    when(climbingSessionRepository.findByUser_UserNum(999L, pageable)).thenReturn(
        new PageImpl<>(List.of()));

    // when
    PagedResponse<CreateSessionResponse> responses = climbingSessionService.getAllClimbingSessions(
        999L, pageable);

    // then
    assertNotNull(responses);
    assertEquals(0, responses.getPage());
  }
}
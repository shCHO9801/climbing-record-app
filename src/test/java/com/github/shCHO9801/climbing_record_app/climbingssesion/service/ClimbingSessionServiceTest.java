package com.github.shCHO9801.climbing_record_app.climbingssesion.service;

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
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession;
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession.Response;
import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.ClimbingSession;
import com.github.shCHO9801.climbing_record_app.climbingssesion.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
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
        .date(YearMonth.of(2025, 1))
        .duration(90)
        .difficultyLevelsCompleted(new HashMap<>())
        .user(user)
        .climbingGym(climbingGym)
        .build();

    climbingSession1 = ClimbingSession.builder()
        .id(1L)
        .date(YearMonth.of(2025, 1))
        .duration(90)
        .difficultyLevelsCompleted(new HashMap<>())
        .user(user)
        .climbingGym(climbingGym)
        .build();

    climbingSession2 = ClimbingSession.builder()
        .id(2L)
        .date(YearMonth.of(2025, 2))
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
    CreateSession.Request request = CreateSession.Request.builder()
        .climbingGymId(1L)
        .userId(100L)
        .date(YearMonth.of(2025, 1))
        .duration(90)
        .difficultyLevels(new HashMap<>())
        .build();

    when(climbingGymRepository.findById(1L)).thenReturn(Optional.of(climbingGym));
    when(userRepository.findByUserNum(100L)).thenReturn(Optional.of(user));
    when(climbingSessionRepository.save(any(ClimbingSession.class))).thenReturn(climbingSession);

    // when
    Response response = climbingSessionService.createClimbingSession(request);

    // then
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals(YearMonth.of(2025, 1), response.getDate());
    assertEquals(90, response.getDuration());
    assertEquals(100L, response.getUserId());
    assertEquals(1L, response.getClimbingGymId());
    assertEquals(climbingGym.getName(), response.getClimbingGymName());

    verify(userMonthlyStatsService, times(1)).aggregateUserMonthlyStats(100L, YearMonth.of(2025, 1),
        90);
  }

  @Test
  @DisplayName("클라이밍 세션 생성 실패 - 클라이밍장이 존재하지 않음")
  public void createClimbingSessionGymNotFound() {
    // given
    CreateSession.Request request = CreateSession.Request.builder()
        .climbingGymId(2L) // 존재하지 않는 클라이밍장 ID
        .userId(100L)
        .date(YearMonth.of(2025, 1))
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
    CreateSession.Request request = CreateSession.Request.builder()
        .climbingGymId(1L)
        .userId(200L) // 존재하지 않는 사용자 ID
        .date(YearMonth.of(2025, 1))
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
    List<ClimbingSession> sessions = Arrays.asList(climbingSession1, climbingSession2);
    when(climbingSessionRepository.findByUser_UserNum(100L)).thenReturn(sessions);

    // when
    List<CreateSession.Response> responses = climbingSessionService.getAllClimbingSessions(100L);

    // then
    assertNotNull(responses);
    assertEquals(2, responses.size());

    CreateSession.Response response1 = responses.get(0);
    assertEquals(1L, response1.getId());
    assertEquals( YearMonth.of(2025, 1), response1.getDate());
    assertEquals(90, response1.getDuration());
    assertEquals(100L, response1.getUserId());
    assertEquals(1L, response1.getClimbingGymId());
    assertEquals("더클라임 강남점", response1.getClimbingGymName());

    CreateSession.Response response2 = responses.get(1);
    assertEquals(2L, response2.getId());
    assertEquals(YearMonth.of(2025, 2), response2.getDate());
    assertEquals(120, response2.getDuration());
    assertEquals(100L, response2.getUserId());
    assertEquals(1L, response2.getClimbingGymId());
    assertEquals("더클라임 강남점", response2.getClimbingGymName());
  }

  @Test
  @DisplayName("모든 클라이밍 세션 조회 성공 - 빈 리스트")
  public void getAllClimbingSessionsEmpty() {
    // given
    when(climbingSessionRepository.findByUser_UserNum(999L)).thenReturn(List.of());

    // when
    List<CreateSession.Response> responses = climbingSessionService.getAllClimbingSessions(999L);

    // then
    assertNotNull(responses);
    assertEquals(0, responses.size());
  }
}
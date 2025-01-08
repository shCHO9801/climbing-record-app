package com.github.shCHO9801.climbing_record_app.climbingsession.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.entity.UserMonthlyStats;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.UserMonthlyStatsRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("월간 기록 유닛 테스트")
public class UserMonthlyStatsServiceTest {

  @InjectMocks
  private UserMonthlyStatsService userMonthlyStatsService;

  @Mock
  private UserMonthlyStatsRepository userMonthlyStatsRepository;

  private UserMonthlyStats userMonthlyStats;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    LocalDate date = LocalDate.of(2025, 1, 1);
    String yearMonth = String.format("%d-%02d", date.getYear(), date.getMonthValue());
    userMonthlyStats = UserMonthlyStats.builder()
        .id(1L)
        .userNum(100L)
        .yearMonth(yearMonth)
        .totalDuration(120)
        .build();
  }

  @Test
  @DisplayName("월간 통계 조회 성공")
  void GetUserMonthlyStatsSuccess() {
    //given
    LocalDate localDate = LocalDate.of(2025, 1, 1);
    String yearMonth = String.format("%d-%02d", localDate.getYear(), localDate.getMonthValue());
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(100L, yearMonth))
        .thenReturn(Optional.of(userMonthlyStats));

    //when
    UserMonthlyStatsResponse response = userMonthlyStatsService
        .getUserMonthlyStats(100L, LocalDate.of(2025, 1, 1));

    //then
    assertNotNull(response);
    assertEquals(100L, response.getUserNum());
    assertEquals(yearMonth, response.getYearMonth());
    assertEquals(120, response.getTotalDuration());
  }

  @Test
  @DisplayName("월간 통계 조회 실패 - 월간 통계 존재하지 않음")
  void GetUserMonthlyStatsFailStatsNotFound() {
    //given
    LocalDate date = LocalDate.of(2025, 1, 1);
    String yearMonth = String.format("%d-%02d", date.getYear(), date.getMonthValue());
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(999L, yearMonth))
        .thenReturn(Optional.empty());

    //when&then
    assertThrows(CustomException.class, () ->
        userMonthlyStatsService.getUserMonthlyStats(999L, date));
  }

  @Test
  @DisplayName("월간 통계 집계 성공")
  void AggregateUserMonthlyStatsSuccess() {
    //given
    LocalDate date = LocalDate.of(2025, 1, 1);
    String yearMonth = String.format("%d-%02d", date.getYear(), date.getMonthValue());
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(100L, yearMonth))
        .thenReturn(Optional.of(userMonthlyStats));

    //when
    userMonthlyStatsService.aggregateUserMonthlyStats(100L, date, 30);

    //then
    assertEquals(150, userMonthlyStats.getTotalDuration());
  }

  @Test
  @DisplayName("월간 통계 집계 - 생성")
  void AggregateUserMonthlyStatsCreateNew() {
    //given
    LocalDate date = LocalDate.of(2025, 1, 1);
    String yearMonth = String.format("%d-%02d", date.getYear(), date.getMonthValue());

    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(200L, yearMonth))
        .thenReturn(Optional.empty());

    //when
    userMonthlyStatsService.aggregateUserMonthlyStats(200L, date, 60);

    //then
    ArgumentCaptor<UserMonthlyStats> captor = ArgumentCaptor.forClass(UserMonthlyStats.class);
    verify(userMonthlyStatsRepository, times(1)).save(captor.capture());

    UserMonthlyStats savedStats = captor.getValue();
    assertEquals(200L, savedStats.getUserNum());
    assertEquals(yearMonth, savedStats.getYearMonth());
    assertEquals(60, savedStats.getTotalDuration());
  }

  @Test
  @DisplayName("페이징된 월간 통계 조회 - 성공")
  void getUserMonthlyStatsPaginatedSuccess() {
    //given
    Pageable pageable = PageRequest.of(0, 2);
    UserMonthlyStats stats1 = UserMonthlyStats.builder()
        .id(1L)
        .userNum(100L)
        .yearMonth("2025-01")
        .totalDuration(120)
        .build();

    UserMonthlyStats stats2 = UserMonthlyStats.builder()
        .id(2L)
        .userNum(100L)
        .yearMonth("2025-02")
        .totalDuration(150)
        .build();

    Page<UserMonthlyStats> page = new PageImpl<>(Arrays.asList(stats1, stats2), pageable, 2);

    when(userMonthlyStatsRepository.findByUserNum(100L, pageable))
        .thenReturn(page);

    //when
    PagedResponse<UserMonthlyStatsResponse> response = userMonthlyStatsService
        .getUserMonthlyStatsPaginated(100L, pageable);

    //then
    assertNotNull(response);
    assertEquals(2, response.getContent().size());
    assertEquals(0, response.getPage());
    assertEquals(2, response.getSize());
    assertEquals(2, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertTrue(response.isLast());

    UserMonthlyStatsResponse response1 = response.getContent().get(0);
    assertEquals(100L, response1.getUserNum());
    assertEquals("2025-01", response1.getYearMonth());
    assertEquals(120, response1.getTotalDuration());

    UserMonthlyStatsResponse response2 = response.getContent().get(1);
    assertEquals(100L, response2.getUserNum());
    assertEquals("2025-02", response2.getYearMonth());
    assertEquals(150, response2.getTotalDuration());
  }

  @Test
  @DisplayName("특정 연도의 월간 통계 조회 성공")
  void getUserMonthlyStatsByYearSuccess() {
    //given
    int year = 2025;
    Pageable pageable = PageRequest.of(0, 2);
    UserMonthlyStats stats1 = UserMonthlyStats.builder()
        .id(1L)
        .userNum(100L)
        .yearMonth("2025-01")
        .totalDuration(120)
        .build();

    UserMonthlyStats stats2 = UserMonthlyStats.builder()
        .id(2L)
        .userNum(100L)
        .yearMonth("2025-02")
        .totalDuration(150)
        .build();

    Page<UserMonthlyStats> page = new PageImpl<>(Arrays.asList(stats1, stats2), pageable, 2);
    when(userMonthlyStatsRepository.findByUserNumAndYearMonthStartingWith(
        100L, "2025-", pageable
    )).thenReturn(page);

    //when
    PagedResponse<UserMonthlyStatsResponse> response =
        userMonthlyStatsService.getUserMonthlyStatsByYear(
            100L, year, pageable);

    //then
    assertNotNull(response);
    assertEquals(2, response.getContent().size());
    assertEquals(0, response.getPage());
    assertEquals(2, response.getSize());
    assertEquals(2, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertTrue(response.isLast());

    UserMonthlyStatsResponse response1 = response.getContent().get(0);
    assertEquals(100L, response1.getUserNum());
    assertEquals("2025-01", response1.getYearMonth());
    assertEquals(120, response1.getTotalDuration());

    UserMonthlyStatsResponse response2 = response.getContent().get(1);
    assertEquals(100L, response2.getUserNum());
    assertEquals("2025-02", response2.getYearMonth());
    assertEquals(150, response2.getTotalDuration());
  }

  @Test
  @DisplayName("특정 연도의 월간 통계 조회 실패 - 데이터 없음")
  void getUserMonthlyStatsByYearPaginatedEmpty() {
    // given
    int year = 2026;
    Pageable pageable = PageRequest.of(0, 2);
    Page<UserMonthlyStats> page = new PageImpl<>(List.of(), pageable, 0);

    when(userMonthlyStatsRepository.findByUserNumAndYearMonthStartingWith(100L, "2026-", pageable))
        .thenReturn(page);

    // when
    PagedResponse<UserMonthlyStatsResponse> response = userMonthlyStatsService.getUserMonthlyStatsByYear(
        100L, year, pageable);

    // then
    assertNotNull(response);
    assertTrue(response.getContent().isEmpty());
    assertEquals(0, response.getPage());
    assertEquals(2, response.getSize());
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
    assertTrue(response.isLast());
  }

}
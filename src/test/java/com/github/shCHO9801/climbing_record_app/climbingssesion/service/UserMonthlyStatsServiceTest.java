package com.github.shCHO9801.climbing_record_app.climbingssesion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.UserMonthlyStats;
import com.github.shCHO9801.climbing_record_app.climbingssesion.repository.UserMonthlyStatsRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserMonthlyStatsServiceTest {

  @InjectMocks
  private UserMonthlyStatsService userMonthlyStatsService;

  @Mock
  private UserMonthlyStatsRepository userMonthlyStatsRepository;

  private UserMonthlyStats userMonthlyStats;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userMonthlyStats = UserMonthlyStats.builder()
        .id(1L)
        .userNum(100L)
        .yearMonth(YearMonth.of(2025, 1))
        .totalDuration(120)
        .build();
  }

  @Test
  @DisplayName("월간 통계 조회 성공")
  void GetUserMonthlyStatsSuccess() {
    //given
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(100L, YearMonth.of(2025, 1)))
        .thenReturn(Optional.of(userMonthlyStats));

    //when
    UserMonthlyStatsResponse response = userMonthlyStatsService
        .getUserMonthlyStats(100L, 2025, 1);

    //then
    assertNotNull(response);
    assertEquals(100L, response.getUserNum());
    assertEquals(YearMonth.of(2025, 1), response.getYearMonth());
    assertEquals(120, response.getTotalDuration());
  }

  @Test
  @DisplayName("월간 통계 조회 실패 - 월간 통계 존재하지 않음")
  void GetUserMonthlyStatsFailStatsNotFound() {
    //given
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(999L, YearMonth.of(2025, 1)))
        .thenReturn(Optional.empty());

    //when&then
    assertThrows(CustomException.class, () ->
        userMonthlyStatsService.getUserMonthlyStats(999L, 2025, 1));
  }

  @Test
  @DisplayName("월간 통계 집계 성공")
  void AggregateUserMonthlyStatsSuccess() {
    //given
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(100L, YearMonth.of(2025, 1)))
        .thenReturn(Optional.of(userMonthlyStats));

    //when
    userMonthlyStatsService.aggregateUserMonthlyStats(100L, YearMonth.of(2025, 1), 30);

    //then
    assertEquals(150, userMonthlyStats.getTotalDuration());
  }

  @Test
  @DisplayName("월간 통계 집계 - 생성")
  void AggregateUserMonthlyStatsCreateNew() {
    //given
    when(userMonthlyStatsRepository.findByUserNumAndYearMonth(200L, YearMonth.of(2025, 1)))
        .thenReturn(Optional.empty());

    //when
    userMonthlyStatsService.aggregateUserMonthlyStats(200L, YearMonth.of(2025,  1), 60);

    //then
    verify(userMonthlyStatsRepository, times(1))
        .save(any(UserMonthlyStats.class));
  }

}
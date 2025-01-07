package com.github.shCHO9801.climbing_record_app.climbingsession.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_MONTHLY_STATS_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.UserMonthlyStatsResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.entity.UserMonthlyStats;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.UserMonthlyStatsRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMonthlyStatsService {

  private final UserMonthlyStatsRepository userMonthlyStatsRepository;

  public UserMonthlyStatsResponse getUserMonthlyStats(
      Long userNum, LocalDate date
  ) {
    String yearMonth = convertDateToString(date);

    UserMonthlyStats stats = userMonthlyStatsRepository
        .findByUserNumAndYearMonth(userNum, yearMonth)
        .orElseThrow(() -> new CustomException(USER_MONTHLY_STATS_NOT_FOUND));

    return UserMonthlyStatsResponse.builder()
        .userNum(stats.getUserNum())
        .yearMonth(stats.getYearMonth())
        .totalDuration(stats.getTotalDuration())
        .build();
  }

  public void aggregateUserMonthlyStats(Long userNum, LocalDate date, int duration) {
    String yearMonth = convertDateToString(date);

    UserMonthlyStats stats = userMonthlyStatsRepository.findByUserNumAndYearMonth(userNum,
            yearMonth)
        .orElse(UserMonthlyStats.builder()
            .userNum(userNum)
            .yearMonth(yearMonth)
            .totalDuration(0)
            .build());

    stats.setTotalDuration(stats.getTotalDuration() + duration);
    userMonthlyStatsRepository.save(stats);
  }

  public PagedResponse<UserMonthlyStatsResponse> getUserMonthlyStatsPaginated(Long userNum,
      Pageable pageable) {
    Page<UserMonthlyStats> page = userMonthlyStatsRepository.findByUserNum(userNum, pageable);
    List<UserMonthlyStatsResponse> content = page.stream()
        .map(this::mapToResponse)
        .toList();

    return PagedResponse.<UserMonthlyStatsResponse>builder()
        .content(content)
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }

  public PagedResponse<UserMonthlyStatsResponse> getUserMonthlyStatsByYear(
      Long userNum, int year, Pageable pageable
  ) {
    String yearPrefix = String.format("%d-", year);
    Page<UserMonthlyStats> page = userMonthlyStatsRepository
        .findByUserNumAndYearMonthStartingWith(userNum, yearPrefix, pageable);
    List<UserMonthlyStatsResponse> content = page.stream()
        .map(this::mapToResponse)
        .toList();

    return PagedResponse.<UserMonthlyStatsResponse>builder()
        .content(content)
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }

  private String convertDateToString(LocalDate date) {
    return String.format("%d-%02d", date.getYear(), date.getMonthValue());
  }

  private UserMonthlyStatsResponse mapToResponse(UserMonthlyStats stats) {
    return UserMonthlyStatsResponse.builder()
        .userNum(stats.getUserNum())
        .yearMonth(stats.getYearMonth())
        .totalDuration(stats.getTotalDuration())
        .build();
  }

}

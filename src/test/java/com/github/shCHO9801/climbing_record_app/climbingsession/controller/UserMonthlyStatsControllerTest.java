package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.shCHO9801.climbing_record_app.climbingsession.entity.UserMonthlyStats;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.UserMonthlyStatsRepository;
import com.github.shCHO9801.climbing_record_app.climbingsession.service.UserMonthlyStatsService;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testH2")
@Transactional
class UserMonthlyStatsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserMonthlyStatsRepository userMonthlyStatsRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClimbingSessionRepository climbingSessionRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private UserMonthlyStatsService userMonthlyStatsService;

  private static User user;

  @BeforeEach
  void setUp() {
    climbingSessionRepository.deleteAll();
    userMonthlyStatsRepository.deleteAll();
    userRepository.deleteAll();
    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .password("testPassword")
        .email("test@test.com")
        .build();

    userService.registerUser(registerRequest);

    user = userRepository.findByUsername("testUser").orElseThrow();

    userMonthlyStatsRepository.saveAll(Arrays.asList(
        UserMonthlyStats.builder()
            .userNum(user.getUserNum())
            .yearMonth("2025-01")
            .totalDuration(120)
            .build(),
        UserMonthlyStats.builder()
            .userNum(user.getUserNum())
            .yearMonth("2025-02")
            .totalDuration(150)
            .build(),
        UserMonthlyStats.builder()
            .userNum(user.getUserNum())
            .yearMonth("2025-03")
            .totalDuration(90)
            .build()
    ));

  }

  @Test
  @DisplayName("사용자 월간 통계 조회 성공")
  void getUserMonthlyStatsSuccess() throws Exception {
    Long userNum = user.getUserNum();

    mockMvc.perform(get("/api/user-monthly-stats")
            .param("userNum", userNum.toString())
            .param("year", "2025")
            .param("month", "01"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userNum").value(userNum))
        .andExpect(jsonPath("$.yearMonth").value("2025-01"))
        .andExpect(jsonPath("$.totalDuration").value(120));
  }

  @Test
  @DisplayName("사용자 월간 통계 조회 실패 - 통계 존재하지 않음")
  void getUserMonthlyStatsFailStatusNotFound() throws Exception {
    mockMvc.perform(get("/api/user-monthly-stats")
            .param("userNum", user.getUserNum().toString())
            .param("year", "2025")
            .param("month", "04"))
        .andExpect(status().isBadRequest());
  }
}
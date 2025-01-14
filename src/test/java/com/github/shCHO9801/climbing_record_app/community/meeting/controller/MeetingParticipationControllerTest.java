package com.github.shCHO9801.climbing_record_app.community.meeting.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.service.MeetingService;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
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
@ActiveProfiles("testH2")
@AutoConfigureMockMvc
@Transactional
@DisplayName("소모임 참여 인테그레이션 테스트")
class MeetingParticipationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private JwtTokenProvider provider;

  @Autowired
  private UserService userService;

  @Autowired
  private MeetingService meetingService;

  @Autowired
  private MeetingRepository meetingRepository;

  @Autowired
  private MeetingParticipationRepository participationRepository;

  private String token;
  private String userId;
  private Long meetingId;

  @BeforeEach
  void setUp() throws Exception {
    // 회원가입
    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .password("password")
        .email("test@test.com")
        .build();
    userService.registerUser(registerRequest);
    userId = "testUser";

    // JWT 토큰 발급
    token = "Bearer " + jwtUtil.generateToken(userId, "USER");

    // 소모임 생성
    CreateMeetingRequest meetingRequest = CreateMeetingRequest.builder()
        .title("Test Meeting")
        .description("Meeting Description")
        .date(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .build();

    String meetingJson = objectMapper.writeValueAsString(meetingRequest);
    String meetingResponse = mockMvc.perform(post("/api/meetings")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(meetingJson))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    meetingId = objectMapper.readTree(meetingResponse).get("id").asLong();
  }

  @Test
  @DisplayName("참여 생성 성공")
  void participationSuccess() throws Exception {
    String response = mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", token))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.meetingId", is(meetingId.intValue())))
        .andExpect(jsonPath("$.userId", is(userId)))
        .andExpect(jsonPath("$.status", is("JOIN")))
        .andReturn().getResponse().getContentAsString();

    // 참여 생성 후, participantCount 업데이트 여부 확인
    // (예를 들어 GET 소모임 조회에서 확인 가능)
  }

  @Test
  @DisplayName("참여 목록 조회 성공 (취소되지 않은 참여만)")
  void getParticipationSuccess() throws Exception {
    // 먼저 참여를 생성합니다.
    mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", token))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/meetings/{meetingId}/participation", meetingId)
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("참여 취소 성공")
  void cancelParticipationSuccess() throws Exception {
    // 먼저 참여 생성
    String response = mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", token))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    Long participationId = objectMapper.readTree(response).get("id").asLong();

    // 취소 요청
    mockMvc.perform(delete("/api/meetings/{meetingId}/participation/{participationId}", meetingId,
            participationId)
            .header("Authorization", token))
        .andExpect(status().isNoContent());
  }
}

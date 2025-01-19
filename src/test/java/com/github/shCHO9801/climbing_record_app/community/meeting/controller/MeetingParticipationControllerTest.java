package com.github.shCHO9801.climbing_record_app.community.meeting.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.community.meeting.service.MeetingService;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
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

  // 호스트(소모임 생성 회원) 관련 변수
  private String hostToken;
  private String hostId = "testUser";
  private Long meetingId;

  // 신규 참여자(호스트와 다른 회원) 관련 변수
  private String participantToken;
  private String participantId = "testUser2";

  @BeforeEach
  void setUp() throws Exception {
    // 기존 데이터를 초기화합니다.
    participationRepository.deleteAll();
    meetingRepository.deleteAll();

    // 호스트 회원가입 및 소모임 생성
    RegisterRequest hostRegister = RegisterRequest.builder()
        .username(hostId)
        .password("password")
        .email("test@test.com")
        .build();
    userService.registerUser(hostRegister);
    hostToken = "Bearer " + jwtUtil.generateToken(hostId, "USER");

    // 소모임 생성 (호스트가 소모임 생성 시 자동 참여 처리됨)
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
            .header("Authorization", hostToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(meetingJson))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    meetingId = objectMapper.readTree(meetingResponse).get("id").asLong();

    // 신규 참여자 회원가입 및 토큰 발급
    RegisterRequest participantRegister = RegisterRequest.builder()
        .username(participantId)
        .password("password")
        .email("test2@test.com")
        .build();
    userService.registerUser(participantRegister);
    participantToken = "Bearer " + jwtUtil.generateToken(participantId, "USER");
  }

  @Test
  @DisplayName("참여 생성 성공")
  void participationSuccess() throws Exception {
    // testUser(호스트)는 이미 참여한 상태이므로, testUser2로 참여 요청합니다.
    String response = mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", participantToken))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.meetingId", is(meetingId.intValue())))
        .andExpect(jsonPath("$.userId", is(participantId)))
        .andExpect(jsonPath("$.status", is("JOIN")))
        .andReturn().getResponse().getContentAsString();

    // 소모임 조회를 통해 participantCount가 증가했는지 확인 (기존 호스트 참여 포함 → 2명)
    String meetingFetchResponse = mockMvc.perform(get("/api/meetings/{id}", meetingId)
            .header("Authorization", hostToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    int participantCount = objectMapper.readTree(meetingFetchResponse).get("participantCount").asInt();
    assertEquals(2, participantCount);
  }

  @Test
  @DisplayName("참여 목록 조회 성공 (취소되지 않은 참여만)")
  void getParticipationSuccess() throws Exception {
    // 먼저 신규 참여자로 참여 생성 (호스트도 이미 자동 참여 중)
    mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", participantToken))
        .andExpect(status().isCreated());

    // 참여 목록 조회 시, 호스트와 신규 참여자 모두가 반환되어야 함
    mockMvc.perform(get("/api/meetings/{meetingId}/participation", meetingId)
            .param("page", "0")
            .param("size", "10")
            .header("Authorization", hostToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  @DisplayName("참여 취소 성공")
  void cancelParticipationSuccess() throws Exception {
    // 신규 참여자로 참여 생성
    String response = mockMvc.perform(post("/api/meetings/{meetingId}/participation", meetingId)
            .header("Authorization", participantToken))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    Long participationId = objectMapper.readTree(response).get("id").asLong();

    // 참여 취소 요청 (신규 참여자 본인이 취소)
    mockMvc.perform(delete("/api/meetings/{meetingId}/participation/{participationId}", meetingId, participationId)
            .header("Authorization", participantToken))
        .andExpect(status().isNoContent());

    // 소모임 조회를 통해 participantCount가 감소했는지 확인 (호스트만 남으므로 1)
    String meetingFetchResponse = mockMvc.perform(get("/api/meetings/{id}", meetingId)
            .header("Authorization", hostToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    int participantCount = objectMapper.readTree(meetingFetchResponse).get("participantCount").asInt();
    assertEquals(1, participantCount);
  }
}

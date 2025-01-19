package com.github.shCHO9801.climbing_record_app.community.meeting.controller;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.dto.UpdateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.service.MeetingParticipationService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("소모임 인테그레이션 테스트")
class MeetingControllerTest {

  private static final Logger log = LoggerFactory.getLogger(MeetingControllerTest.class);
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserService userService;

  @Autowired
  private MeetingParticipationService meetingParticipationService;


  private String token;
  private String userId;
  private Long meetingId;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private MeetingParticipationRepository meetingParticipationRepository;
  @Autowired
  private MeetingRepository meetingRepository;

  @BeforeEach
  void setUp() throws Exception {
    meetingParticipationRepository.deleteAll();
    meetingRepository.deleteAll();
    userRepository.deleteAll();

    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .password("password")
        .email("test@test.com")
        .build();

    userService.registerUser(registerRequest);
    User user = userRepository.findByUsername("testUser")
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    userId = user.getId();
    log.info("userID : {}", userId);
    token = "Bearer " + jwtUtil.generateToken(userId, Role.USER.toString());
    log.info("token : {}", token);

    CreateMeetingRequest meetingRequest = CreateMeetingRequest.builder()
        .title("testMeeting")
        .description("testDescription")
        .date(LocalDate.from(LocalDateTime.now().plusDays(1)))
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

    JsonNode jsonNode = objectMapper.readTree(meetingResponse);
    meetingId = jsonNode.get("id").asLong();
    log.info("---meetingId: {}", meetingId);
  }

  @Test
  @DisplayName("소모임 조회 성공")
  void getMeetingsSuccess() throws Exception {
    mockMvc.perform(get("/api/meetings")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].title").value("testMeeting"));
  }

  @Test
  @DisplayName("소모임 수정 성공")
  void updateMeetingSuccess() throws Exception {
    UpdateMeetingRequest updateRequest = UpdateMeetingRequest.builder()
        .title("updateMeeting")
        .capacity(6)
        .build();
    String updateJson = objectMapper.writeValueAsString(updateRequest);

    mockMvc.perform(put("/api/meetings/{meetingId}", meetingId)
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("updateMeeting"));
  }

  @Test
  @DisplayName("소모임 삭제 성공")
  void deleteMeetingSuccess() throws Exception {
    mockMvc.perform(delete("/api/meetings/{meetingId}", meetingId)
        .header("Authorization", token))
        .andExpect(status().isNoContent());
  }
}
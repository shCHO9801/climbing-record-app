package com.github.shCHO9801.climbing_record_app.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
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
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("계정 생성 성공")
  void registerSuccess() throws Exception {
    // given
    RegisterRequest registerRequest = buildRegisterRequest(
        "id", "pw", "email@email.com"
    );

    // when
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("회원가입이 완료되었습니다."));

    // then
    boolean exists = userRepository.existsById(registerRequest.getUsername());
    User user = userRepository.findByUsername(registerRequest.getUsername())
        .orElse(null);

    // 사용자가 데이터 베이스에 존재해야 한다.
    assertTrue(exists);
    // 사용자 ID가 일치해야 한다.
    assertEquals(registerRequest.getUsername(), user.getId());
    // 사용자 이메일이 일치해야 한다.
    assertEquals(registerRequest.getEmail(), user.getEmail());
  }

  @Test
  @DisplayName("계정 생성 실패 - 중복된 ID")
  void duplicateId() throws Exception {
    // given
    RegisterRequest registerRequest = buildRegisterRequest(
        "duplicateId", "pw", "email@email.com"
    );

    // when & then
    // 첫 번째 등록 시도
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("회원가입이 완료되었습니다."));

    // 중복된 사용자명으로 두 번째 등록 시도
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("이미 존재하는 ID 입니다."));
  }

  @Test
  @DisplayName("로그인 성공")
  void loginSuccess() throws Exception {
    // given
    RegisterRequest registerRequest = buildRegisterRequest(
        "loginUser", "loginPw", "login@example.com"
    );

    // 회원가입
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("회원가입이 완료되었습니다."));

    AuthRequest authRequest = buildAuthRequest(
        registerRequest.getUsername(), registerRequest.getPassword()
    );

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 비밀번호")
  void loginFailWrongPassword() throws Exception {
    // given
    RegisterRequest registerRequest = buildRegisterRequest(
        "loginFailUser", "correctPw", "loginfail@example.com"
    );

    // 회원가입
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("회원가입이 완료되었습니다."));

    AuthRequest authRequest = buildAuthRequest(
        registerRequest.getUsername(), "wrongPw"
    );

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
  }

  private RegisterRequest buildRegisterRequest(
      String username, String password, String email
  ) {
    return RegisterRequest.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();
  }

  private AuthRequest buildAuthRequest(String username, String password) {
    return AuthRequest.builder()
        .username(username)
        .password(password)
        .build();
  }
}
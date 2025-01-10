package com.github.shCHO9801.climbing_record_app.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.Map;
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
@Transactional
@ActiveProfiles("testH2")
class ProfileControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  private String jwtToken;
  private User user;

  @BeforeEach
  void setUp() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUsername")
        .password("testPassword")
        .email("testEmail")
        .build();

    userService.registerUser(registerRequest);

    user = userRepository.findByUsername("testUsername").orElseThrow();

    jwtToken = jwtUtil.generateToken(user.getId(), Role.USER.toString());
  }

  @Test
  @DisplayName("프로필 조회 성공")
  void getProfileSuccess() throws Exception {
    mockMvc.perform(get("/api/profile")
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value("testEmail"));
  }

  @Test
  @DisplayName("프로필 업데이트 성공")
  void updateProfileSuccess() throws Exception {
    ProfileRequest updateRequest = ProfileRequest.builder()
        .nickname("Nick")
        .armLength(170.0)
        .height(170.0)
        .equipmentInfo(Map.of("암벽화", "Drago"))
        .build();

    mockMvc.perform(put("/api/profile")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.nickname").value("Nick"))
        .andExpect(jsonPath("$.height").value(170.0))
        .andExpect(jsonPath("$.armLength").value(170.0))
        .andExpect(jsonPath("$.equipmentInfo.암벽화").value("Drago"));

    // 데이터베이스에서 업데이트된 사용자 정보 확인
    User updatedUser = userRepository.findByUsername("testUsername").orElseThrow();
    assertEquals("Nick", updatedUser.getNickname());
    assertEquals(170.0, updatedUser.getArmLength());
    assertEquals(170.0, updatedUser.getHeight());
    assertEquals(Map.of("암벽화", "Drago"), updatedUser.getEquipmentInfo());
  }

  @Test
  @DisplayName("프로필 업데이트 실패 - 유효하지 않은 토큰")
  void updateProfileFailInvalidToken() throws Exception {
    ProfileRequest updateRequest = ProfileRequest.builder()
        .nickname("Nick")
        .armLength(170.0)
        .height(170.0)
        .equipmentInfo(Map.of("암벽화", "Drago"))
        .build();

    mockMvc.perform(put("/api/profile")
            .header("Authorization", "Bearer dds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("유효하지 않은 JWT 토큰입니다."));
  }

  @Test
  @DisplayName("프로필 업데이트 실패 - 토큰 없음")
  void updateProfileFailNoToken() throws Exception {
    ProfileRequest updateRequest = ProfileRequest.builder()
        .nickname("Nick")
        .armLength(170.0)
        .height(170.0)
        .equipmentInfo(Map.of("암벽화", "Drago"))
        .build();

    mockMvc.perform(put("/api/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("JWT 토큰이 존재하지 않거나 형식이 잘못되었습니다."));
  }
}
package com.github.shCHO9801.climbing_record_app.community.posting.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGymRequest;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbinggym.service.ClimbingGymService;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.UpdatePostRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import jakarta.transaction.Transactional;
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
@DisplayName("게시글 인테그레이션 테스트")
class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserService userService;

  @Autowired
  private ClimbingGymService climbingGymService;

  private String token;
  private User user;
  private ClimbingGym gym;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ClimbingGymRepository climbingGymRepository;

  @BeforeEach
  void setUp() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .email("test@test.com")
        .password("testPassword")
        .build();

    userService.registerUser(registerRequest);

    CreateGymRequest createGymRequest = CreateGymRequest.builder()
        .name("testGym")
        .latitude(123.0)
        .longitude(456.0)
        .price(20000)
        .build();

    climbingGymService.createClimbingGym(createGymRequest);

    user = userRepository.findByUsername("testUser").orElseThrow();
    gym = climbingGymRepository.findByName("testGym");

    token = "Bearer " + jwtUtil.generateToken("testUser", Role.USER.toString());
  }

  @Test
  @DisplayName("게시글 생성 및 조회 성공")
  void createAndGetPostSuccess() throws Exception {
    CreatePostRequest createPostRequest = CreatePostRequest.builder()
        .title("title")
        .content("content")
        .climbingGymId(gym.getId())
        .media(null)
        .build();

    String createResponse = mockMvc.perform(post("/api/posts")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createPostRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.postId").isNumber())
        .andExpect(jsonPath("$.message").value("게시글이 성공적으로 생성되었습니다."))
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].title").value("title"))
        .andExpect(jsonPath("$.content[0].content").value("content"))
        .andExpect(jsonPath("$.totalElements").value("1"));
  }

  @Test
  @DisplayName("게시글 수정 및 삭제 성공")
  void updateAndDeletePostSuccess() throws Exception {
    CreatePostRequest createRequest = CreatePostRequest.builder()
        .title("Original Title")
        .content("Original Content")
        .climbingGymId(1L)
        .media(null)
        .build();

    String createResponse = mockMvc.perform(post("/api/posts")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    StringBuilder sb = new StringBuilder();
    for (char c : createResponse.toCharArray()) {
      if (Character.isDigit(c)) {
        sb.append(c);
      }
    }

    Long postId = Long.parseLong(sb.toString());

    UpdatePostRequest updateRequest = UpdatePostRequest.builder()
        .title("Updated Title")
        .content("Updated Content")
        .build();

    mockMvc.perform(put("/api/posts/" + postId)
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId").value(postId))
        .andExpect(jsonPath("$.message").value("게시글이 성공적으로 수정되었습니다."));

    mockMvc.perform(delete("/api/posts/" + postId)
            .header("Authorization", token))
        .andExpect(status().isNoContent());
  }
}
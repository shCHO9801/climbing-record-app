package com.github.shCHO9801.climbing_record_app.community.comment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGymRequest;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGymResponse;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbinggym.service.ClimbingGymService;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.CreateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.UpdateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.service.PostService;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("testContainer")
@Testcontainers
@AutoConfigureMockMvc
@Transactional
@DisplayName("댓글 인테그레이션 테스트")
class CommentControllerTest {

  @Container
  private static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.28")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
    registry.add("spring.datasource.username", mysql::getUsername);
    registry.add("spring.datasource.password", mysql::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.properties.hibernate.dialect",
        () -> "org.hibernate.dialect.MySQL8Dialect");
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ClimbingGymRepository climbingGymRepository;

  @Autowired
  private ClimbingGymService climbingGymService;

  private String token;
  private User user;
  private Post post;
  private ClimbingGym gym;
  @Autowired
  private PostService postService;

  @BeforeEach
  void setUp() throws Exception {
    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .password("testPassword")
        .email("test@test.com")
        .build();

    userService.registerUser(registerRequest);
    user = userRepository.findByUsername("testUser")
        .orElse(null);

    token = "Bearer " + jwtUtil.generateToken(user.getId(), user.getRole().toString());

    CreateGymRequest createGymRequest = CreateGymRequest.builder()
        .name("testGym")
        .longitude(123.0)
        .latitude(4566.0)
        .price(20000)
        .build();
    CreateGymResponse createGymResponse
        = climbingGymService.createClimbingGym(createGymRequest);
    gym = climbingGymRepository.findById(createGymResponse.getId())
        .orElse(null);

    CreatePostRequest createPostRequest = CreatePostRequest.builder()
        .title("testPost")
        .content("testContent")
        .climbingGymId(gym.getId())
        .build();

    post = postService.createPost(user.getId(), createPostRequest);
  }

  @Test
  @DisplayName("댓글 생성 성공")
  void createCommentSuccess() throws Exception {
    CreateCommentRequest request = CreateCommentRequest.builder()
        .content("새로운 댓글.")
        .build();

    mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("새로운 댓글."));
  }

  @Test
  @DisplayName("댓글 목록 조회 성공")
  void getCommentsSuccess() throws Exception {
    CreateCommentRequest commentRequest = CreateCommentRequest.builder()
        .content("댓글 1")
        .build();
    mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/posts/{postId}/comments", post.getId())
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].content").value("댓글 1"));
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void updateCommentSuccess() throws Exception {
    CreateCommentRequest commentRequest = CreateCommentRequest.builder()
        .content("원래 댓글")
        .build();
    String responseContent = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    System.out.println(responseContent);

    JsonNode node = objectMapper.readTree(responseContent);
    Long commentId = node.get("id").asLong();

    UpdateCommentRequest updateRequest = UpdateCommentRequest.builder()
        .content("수정된 댓글")
        .build();

    mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 댓글"));
  }

  @Test
  @DisplayName("댓글 삭제 성공")
  void deleteCommentSuccess() throws Exception {
    CreateCommentRequest createRequest = CreateCommentRequest.builder()
        .content("삭제할 댓글")
        .build();
    String responseContent = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    JsonNode node = objectMapper.readTree(responseContent);
    Long commentId = node.get("id").asLong();

    mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
            .header("Authorization", token))
        .andExpect(status().isNoContent());
  }
}
package com.github.shCHO9801.climbing_record_app.climbingsession.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionRequest;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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
@AutoConfigureMockMvc
@ActiveProfiles("testContainer")
@Testcontainers
@Transactional
@DisplayName("운동 세션 인테그레이션 테스트")
class ClimbingSessionControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  private ClimbingSessionRepository climbingSessionRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ClimbingGymRepository climbingGymRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserService userService;

  private ClimbingGym climbingGym;
  private User user;


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

  @BeforeEach
  void setUp() {
    climbingSessionRepository.deleteAll();
    userRepository.deleteAll();
    climbingGymRepository.deleteAll();

    GeometryFactory gf = new GeometryFactory();
    climbingGym = ClimbingGym.builder()
        .name("Climbing Gym")
        .price(23000)
        .location(gf.createPoint(new Coordinate(127, 37)))
        .difficultyChart(Arrays.asList("하양", "노랑", "주황"))
        .build();
    climbingGymRepository.save(climbingGym);

    RegisterRequest registerRequest = RegisterRequest.builder()
        .username("testUser")
        .password("testPassword")
        .email("test@test.com")
        .build();

    userService.registerUser(registerRequest);

    user = userRepository.findByUsername("testUser").orElseThrow();
  }

  @Test
  @DisplayName("클라이밍 세션 생성 및 통합 테스트 - 성공")
  void createAndGetClimbingSession() throws Exception {
    CreateSessionRequest request = CreateSessionRequest.builder()
        .climbingGymId(climbingGym.getId())
        .userId(user.getUserNum())
        .date(LocalDate.of(2025, 1, 1))
        .duration(90)
        .difficultyLevels(new HashMap<>())
        .build();

    mockMvc.perform(post("/api/climbing-session")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.date").value("2025-01-01"))
        .andExpect(jsonPath("$.duration").value(90))
        .andExpect(jsonPath("$.userId").value(user.getUserNum()))
        .andExpect(jsonPath("$.climbingGymId").value(climbingGym.getId()))
        .andExpect(jsonPath("$.climbingGymName").value(climbingGym.getName()));

    // 클라이밍 세션 조회 요청
    mockMvc.perform(get("/api/climbing-session")
            .param("userNum", String.valueOf(user.getUserNum()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].date").value("2025-01-01"))
        .andExpect(jsonPath("$.content[0].duration").value(90))
        .andExpect(jsonPath("$.content[0].userId").value(user.getUserNum()))
        .andExpect(jsonPath("$.content[0].climbingGymId").value(climbingGym.getId()))
        .andExpect(jsonPath("$.content[0].climbingGymName").value(climbingGym.getName()));
  }
}
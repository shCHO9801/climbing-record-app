package com.github.shCHO9801.climbing_record_app.climbinggym.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbinggym.service.ClimbingGymService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("testContainer")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("클라이밍장 인테그레이션 테스트")
class ClimbingGymControllerTest {

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
  private ClimbingGymRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ClimbingGymService service;

  private CreateGym.Request request;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    GeometryFactory geometryFactory = new GeometryFactory();
    request = CreateGym.Request.builder()
        .name("더클라임 강남점")
        .location(geometryFactory.createPoint(new Coordinate(127.0, 37.0)))
        .price(23000)
        .parkingInfo("30분 무료, 이후 30분당 3000원")
        .difficultyChart(Arrays.asList("하양", "노랑", "주황", "초록", "파랑", "빨강", "보라", "회색", "갈색", "검정"))
        .amenities("세족실")
        .build();
  }

  @Test
  @DisplayName("클라이밍장 생성 성공")
  void createGymSuccess() throws Exception {
    //given
    String requestBody = objectMapper.writeValueAsString(request);

    //when&then
    mockMvc.perform(post("/api/gyms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.location.type").value("point"))
        .andExpect(jsonPath("$.location.coordinates[0]").value(127.0))
        .andExpect(jsonPath("$.location.coordinates[1]").value(37.0))
        .andExpect(jsonPath("$.createdAt").exists());

    boolean exists = repository.existsByName(request.getName());
    ClimbingGym gym = repository.findByName(request.getName());

    assertTrue(exists);
    assertNotNull(gym);
    assertEquals(gym.getName(), request.getName());
    assertEquals(gym.getLocation().getX(), request.getLocation().getX());
    assertEquals(gym.getLocation().getY(), request.getLocation().getY());
    assertNotNull(gym.getCreatedAt());
  }

  @Test
  @DisplayName("클라이밍장 생성 실패 - 중복된 이름으로 생성")
  void createGymFailAlreadyExists() throws Exception {
    //given
    service.createClimbingGym(request);

    String requestBody = objectMapper.writeValueAsString(request);

    //when&then
    mockMvc.perform(
            post("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message")
                .value("이미 존재하는 클라이밍장 입니다.")
        );
  }

  @Test
  @DisplayName("클라이밍장 조회 성공")
  void getAllClimbingGymSuccess() throws Exception {
    //given
    service.createClimbingGym(request);

    ClimbingGym newGym = ClimbingGym.builder()
        .name("더클라임 홍대점")
        .location(new GeometryFactory().createPoint(new Coordinate(126.0, 37.0)))
        .price(23000)
        .parkingInfo("무료 주차")
        .difficultyChart(Arrays.asList("하양", "노랑", "주황", "초록", "파랑", "빨강", "보라", "회색", "갈색", "검정"))
        .amenities("샤워실")
        .build();

    repository.save(newGym);

    //when&then
    mockMvc.perform(get("/api/gyms")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].name").value(request.getName()))
        .andExpect(jsonPath("$.content[1].name").value(newGym.getName()));
  }
}
package com.github.shCHO9801.climbing_record_app.climbinggym.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym.Request;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.GetGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ClimbingGymServiceTest {

  @InjectMocks
  private ClimbingGymService climbingGymService;

  @Mock
  private ClimbingGymRepository repository;

  private CreateGym.Request request;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
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
  void createClimbingGymSuccess() {
    //given
    when(repository.existsByName(request.getName())).thenReturn(false);

    ClimbingGym savedGym = makeGym(request);

    when(repository.save(any(ClimbingGym.class))).thenReturn(savedGym);

    //when
    CreateGym.Response response = climbingGymService.createClimbingGym(request);

    //then
    assertNotNull(response);
    assertEquals(response.getId(), savedGym.getId());
    assertEquals(response.getName(), savedGym.getName());
    assertEquals(response.getLocation(), savedGym.getLocation());
    assertNotNull(response.getCreatedAt());

    verify(repository, times(1)).existsByName(request.getName());
    verify(repository, times(1)).save(any(ClimbingGym.class));
  }

  @Test
  @DisplayName("클라이밍장 생성 실패 - 이미 존재하는 클라이밍장")
  void createClimbingGymFailedAlreadyExist() {
    //given
    when(repository.existsByName(request.getName())).thenReturn(true);

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> climbingGymService.createClimbingGym(request));

    assertEquals(CLIMBING_GYM_ALREADY_EXISTS, exception.getErrorCode());

    verify(repository, times(1)).existsByName(request.getName());
    verify(repository, times(0)).save(any(ClimbingGym.class));
  }

  @Test
  @DisplayName("getAllGyms 성공")
  void getAllGymsSuccess() {
    //given
    ClimbingGym gym1 = makeGym(request);

    ClimbingGym gym2 = makeGym(
        2L,
        "더클라임 홍대점",
        new GeometryFactory().createPoint(new Coordinate(11, 11)),
        20000
    );

    ClimbingGym gym3 = makeGym(
        3L,
        "더클라임 연남점",
        new GeometryFactory().createPoint(new Coordinate(22, 22)),
        24000
    );

    List<ClimbingGym> gyms = Arrays.asList(gym1, gym2, gym3);
    when(repository.findAll()).thenReturn(gyms);

    //when
    List<GetGym> response = climbingGymService.getAllGyms();

    //then
    assertNotNull(response);
    assertEquals(response.size(), 3);

    for (int i = 0; i < response.size(); i++) {
      assertEquals(response.get(i).getId(), gyms.get(i).getId());
      assertEquals(response.get(i).getName(), gyms.get(i).getName());
      assertEquals(response.get(i).getLocation(), gyms.get(i).getLocation());
      assertEquals(response.get(i).getPrice(), gyms.get(i).getPrice());
      assertEquals(response.get(i).getDifficultyChart(), gyms.get(i).getDifficultyChart());
      assertEquals(response.get(i).getAmenities(), gyms.get(i).getAmenities());
      assertNotNull(response.get(i).getCreatedAt());
    }
  }

  private ClimbingGym makeGym(Request request) {
    return ClimbingGym.builder()
        .id(1L)
        .name(request.getName())
        .location(request.getLocation())
        .price(request.getPrice())
        .parkingInfo(request.getParkingInfo())
        .difficultyChart(request.getDifficultyChart())
        .amenities(request.getAmenities())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ClimbingGym makeGym(Long id, String name, Point location, int price) {
    return ClimbingGym.builder()
        .id(id)
        .name(name)
        .location(location)
        .price(price)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}

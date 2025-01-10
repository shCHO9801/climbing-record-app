package com.github.shCHO9801.climbing_record_app.user.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileResponse;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("유저 프로필 유닛 테스트")
class ProfileServiceTest {

  @InjectMocks
  private ProfileService profileService;

  private User user;
  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Map<String, Object> equipmentInfo = new HashMap<>();
    equipmentInfo.put("암벽화", "드라고");
    user = User.builder()
        .userNum(1L)
        .id("testUser")
        .password("testPassword")
        .email("user100@example.com")
        .role(USER)
        .nickname("Tester")
        .height(175.0)
        .armLength(80.0)
        .equipmentInfo(equipmentInfo)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("프로필 조회 성공")
  void getProfileSuccess() {
    //given
    userRepository.save(user);
    when(userRepository.findByUsername(user.getId())).thenReturn(Optional.of(user));

    //when
    ProfileResponse response = profileService.getProfile(user.getId());

    //then
    assertEquals(user.getId(), response.getId());
    assertEquals(user.getEmail(), response.getEmail());
    assertEquals(user.getNickname(), response.getNickname());
    assertEquals(user.getHeight(), response.getHeight());
    assertEquals(user.getArmLength(), response.getArmLength());
    assertEquals(user.getEquipmentInfo(), response.getEquipmentInfo());
  }

  @Test
  @DisplayName("프로필 조회 실패 - 유저 미존재")
  void getProfileUserNotFound() {
    // given
    when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      profileService.getProfile("nonExistentUser");
    });
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("프로필 업데이트 성공")
  void updateProfileSuccess() {
    // given
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    ProfileRequest request = ProfileRequest.builder()
        .nickname("Nick")
        .armLength(170.0)
        .height(170.0)
        .equipmentInfo(Map.of("암벽화", "Drago"))
        .build();

    // when
    ProfileResponse response = profileService.updateProfile("testUser", request);

    // then
    assertEquals("Nick", response.getNickname());
    assertEquals(170.0, response.getArmLength());
    assertEquals(170.0, response.getHeight());
    assertEquals(Map.of("암벽화", "Drago"), response.getEquipmentInfo());

    verify(userRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("프로필 업데이트 실패 - 유저 미존재")
  void updateProfileUserNotFound() {
    // given
    when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

    ProfileRequest request = ProfileRequest.builder()
        .nickname("Nick")
        .armLength(170.0)
        .height(170.0)
        .equipmentInfo(Map.of("암벽화", "Drago"))
        .build();

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      profileService.updateProfile("nonExistentUser", request);
    });
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

}
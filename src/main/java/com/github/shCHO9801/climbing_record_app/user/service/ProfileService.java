package com.github.shCHO9801.climbing_record_app.user.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileResponse;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final UserRepository userRepository;

  public ProfileResponse getProfile(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    return createResponse(user);
  }

  public ProfileResponse updateProfile(String username, ProfileRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    if (request.getNickname() != null) {
      user.setNickname(request.getNickname());
    }
    if (request.getArmLength() != null) {
      user.setArmLength(request.getArmLength());
    }
    if (request.getHeight() != null) {
      user.setHeight(request.getHeight());
    }
    if (request.getEquipmentInfo() != null) {
      user.setEquipmentInfo(request.getEquipmentInfo());
    }
    User saved = userRepository.save(user);

    return createResponse(saved);
  }

  private ProfileResponse createResponse(User user) {
    return ProfileResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .height(user.getHeight())
        .armLength(user.getArmLength())
        .equipmentInfo(user.getEquipmentInfo())
        .build();
  }
}

package com.github.shCHO9801.climbing_record_app.user.controller;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.ProfileResponse;
import com.github.shCHO9801.climbing_record_app.user.service.ProfileService;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final JwtTokenProvider provider;
  private final ProfileService profileservice;

  @GetMapping
  public ResponseEntity<ProfileResponse> getProfile(
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    String userId = extractUserId(authorizationHeader);

    ProfileResponse profile = profileservice.getProfile(userId);
    return ResponseEntity.ok(profile);
  }

  @PutMapping
  public ResponseEntity<ProfileResponse> updateProfile(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody ProfileRequest profileRequest
  ) {
    String userId = extractUserId(authorizationHeader);

    ProfileResponse response = profileservice.updateProfile(userId, profileRequest);
    return ResponseEntity.ok(response);
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }
}

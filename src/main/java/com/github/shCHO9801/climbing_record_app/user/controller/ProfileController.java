package com.github.shCHO9801.climbing_record_app.user.controller;

import com.github.shCHO9801.climbing_record_app.user.dto.ProfileDto;
import com.github.shCHO9801.climbing_record_app.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
  private final ProfileService profileservice;

  @GetMapping
  public ResponseEntity<ProfileDto.Response> getProfile(
      Authentication authentication) {

    String username = authentication.getName();
    ProfileDto.Response profile = profileservice.getProfile(username);
    return ResponseEntity.ok(profile);
  }

  @PutMapping
  public ResponseEntity<ProfileDto.Response> updateProfile(
      @RequestBody ProfileDto.Request request,
      Authentication authentication
  ) {
    String username = authentication.getName();
    ProfileDto.Response response = profileservice.updateProfile(username, request);
    return ResponseEntity.ok(response);
  }
}

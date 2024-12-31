package com.github.shCHO9801.climbing_record_app.user.controller;

import com.github.shCHO9801.climbing_record_app.user.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    AuthResponse response = userService.loginUser(authRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    userService.registerUser(registerRequest);
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }
}

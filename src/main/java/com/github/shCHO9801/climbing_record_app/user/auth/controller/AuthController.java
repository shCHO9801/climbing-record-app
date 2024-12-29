package com.github.shCHO9801.climbing_record_app.user.auth.controller;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;

import com.github.shCHO9801.climbing_record_app.user.auth.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.auth.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.user.auth.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.auth.jwt.JwtUtil;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoggingUtil loggingUtil;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    loggingUtil.logRequest("로그인", authRequest);

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUsername(), authRequest.getPassword())
      );
      String username = authentication.getName();

      String token = jwtUtil.generateToken(username, "USER");
      loggingUtil.logSuccess("로그인", "JWT 토큰 발급 성공");

      return ResponseEntity.ok(new AuthResponse(token));
    } catch (BadCredentialsException e) {
      loggingUtil.logError("로그인", "잘못된 자격 증명");
      throw new CustomException(USER_NOT_FOUND);
    } catch (Exception e) {
      loggingUtil.logError("로그인", "로그인 실패: " + e.getMessage());
      throw new CustomException(USER_NOT_FOUND);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

    loggingUtil.logRequest("회원가입", registerRequest);

    if (userRepository.existsById(registerRequest.getUsername())) {
      loggingUtil.logError("회원가입", registerRequest.getUsername() + ": 이미 존재하는 사용자명");
      throw new CustomException(USER_ALREADY_EXISTS);
    }

    User user = User.builder()
        .id(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .email(registerRequest.getEmail())
        .role(USER)
        .build();

    userRepository.save(user);

    loggingUtil.logSuccess("회원가입", user.getId() + ": 회원가입 성공");

    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }
}

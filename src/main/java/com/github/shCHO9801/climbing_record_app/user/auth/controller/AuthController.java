package com.github.shCHO9801.climbing_record_app.user.auth.controller;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.auth.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.auth.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.user.auth.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    logger.info("로그인 시도: 사용자명={}", authRequest.getUsername());

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUsername(), authRequest.getPassword())
      );
      String username = authentication.getName();

      String token = jwtUtil.generateToken(username, "USER");
      logger.info("로그인 성공: 사용자명={}, JWT 토큰 발급", username);

      return ResponseEntity.ok(new AuthResponse(token));
    } catch (Exception e) {
      logger.error("로그인 실패: 잘못된 자격 증명 - 사용자명-{}", authRequest.getUsername());
      throw new CustomException(USER_NOT_FOUND);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

    logger.info("회원가입 시도: 사용자명-{}, 이메일={}", registerRequest.getUsername(), registerRequest.getPassword());

    if (userRepository.existsById(registerRequest.getUsername())) {
      logger.error("회원가입 실패: 이미 존재하는 사용자명={}", registerRequest.getUsername());
      throw new CustomException(USER_ALREADY_EXISTS);
    }

    User user = User.builder()
        .id(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .email(registerRequest.getEmail())
        .role(USER)
        .build();

    userRepository.save(user);

    logger.info("회원가입 성공: 사용자명={}", registerRequest.getUsername());
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }
}

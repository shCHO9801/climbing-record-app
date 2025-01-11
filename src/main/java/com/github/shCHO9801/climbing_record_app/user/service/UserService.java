package com.github.shCHO9801.climbing_record_app.user.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public AuthResponse loginUser(AuthRequest authRequest) {
    logger.info("로그인 시도: 사용자명={}", authRequest.getUsername());

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUsername(), authRequest.getPassword())
      );
      String username = authentication.getName();

      String token = jwtUtil.generateToken(username, "USER");
      String bearerToken = "Bearer " + token;
      logger.info("로그인 성공: 사용자명={}, JWT 토큰 발급", username);

      return new AuthResponse(bearerToken);
    } catch (Exception e) {
      logger.error("로그인 실패: 잘못된 자격 증명 - 사용자명-{}", authRequest.getUsername());
      throw new CustomException(USER_NOT_FOUND);
    }
  }

  public void registerUser(RegisterRequest request) {
    logger.info("회원가입 시도: 사용자명-{}, 이메일={}", request.getUsername(),
        request.getEmail());

    if (userRepository.existsById(request.getUsername())) {
      logger.error("회원가입 실패: 이미 존재하는 사용자명={}", request.getUsername());
      throw new CustomException(USER_ALREADY_EXISTS);
    }

    User user = User.builder()
        .id(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .email(request.getEmail())
        .role(USER)
        .build();

    userRepository.save(user);

    logger.info("회원가입 성공: 사용자명={}", request.getUsername());
  }
}

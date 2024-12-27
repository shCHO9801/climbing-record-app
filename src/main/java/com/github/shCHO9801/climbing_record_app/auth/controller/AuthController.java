package com.github.shCHO9801.climbing_record_app.auth.controller;

import com.github.shCHO9801.climbing_record_app.auth.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.auth.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.auth.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.auth.jwt.JwtUtil;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
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

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUsername(), authRequest.getPassword())
      );

      User user = userRepository.findByUsername(authRequest.getUsername())
          .orElseThrow();

      String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

      return ResponseEntity.ok(new AuthResponse(token));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401).body("Invalid credentials");
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    User user = User.builder()
        .id(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .email(registerRequest.getEmail())
        .role(Role.USER)
        .build();

    userRepository.save(user);

    return ResponseEntity.ok("User registered successfully");
  }
}

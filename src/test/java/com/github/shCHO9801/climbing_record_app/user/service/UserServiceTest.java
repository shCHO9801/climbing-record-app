package com.github.shCHO9801.climbing_record_app.user.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthRequest;
import com.github.shCHO9801.climbing_record_app.user.dto.AuthResponse;
import com.github.shCHO9801.climbing_record_app.user.dto.RegisterRequest;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import com.github.shCHO9801.climbing_record_app.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("로그인 성공")
  void loginSuccess() {
    //given
    AuthRequest request = createAuthRequest("testUser", "testPass");

    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(request.getUsername());
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtUtil.generateToken(request.getUsername(), "USER"))
        .thenReturn("jwtToken");
    //when
    AuthResponse response = userService.loginUser(request);

    //then
    assertNotNull(response);
    assertEquals("jwtToken", response.getToken());

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtil, times(1)).generateToken(request.getUsername(), "USER");
  }

  @Test
  void loginFailedException() {
    //given
    AuthRequest request = createAuthRequest("testUser", "testPass");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("bad password"));

    // When & Then
    CustomException exception = assertThrows(CustomException.class, () -> {
      userService.loginUser(request);
    });

    assertEquals(USER_NOT_FOUND, exception.getErrorCode());

    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtil, never()).generateToken(anyString(), anyString());

  }


  @Test
  @DisplayName("회원가입 성공")
  void registerSuccess() {
    //given
    RegisterRequest request = createRegisterRequest(
        "testUser",
        "testPassword",
        "testEmail");

    when(userRepository.existsById("testUser")).thenReturn(false);
    when(passwordEncoder.encode("testPassword")).thenReturn("encodedPass");

    User savedUser = createUser(
        request.getUsername(),
        request.getPassword(),
        request.getEmail()
    );

    when(userRepository.save(savedUser)).thenReturn(savedUser);

    //when
    userService.registerUser(request);

    //then
    verify(userRepository, times(1)).existsById("testUser");
    verify(passwordEncoder, times(1)).encode("testPassword");
    verify(userRepository, times(1)).save(savedUser);
  }


  @Test
  @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
  void registerFailUserAlreadyExists() {
    //given
    RegisterRequest request = createRegisterRequest(
        "exist",
        "pw",
        "email"
    );

    when(userRepository.existsById(request.getUsername())).thenReturn(true);

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.registerUser(request));

    assertEquals(USER_ALREADY_EXISTS, exception.getErrorCode());

    verify(userRepository, times(1)).existsById(request.getUsername());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  private AuthRequest createAuthRequest(String username, String password) {
    return AuthRequest.builder()
        .username(username)
        .password(password)
        .build();
  }

  private static RegisterRequest createRegisterRequest(
      String username, String password, String email
  ) {
    return RegisterRequest.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();
  }

  private User createUser(
      String username, String password, String email) {
    return User.builder()
        .id(username)
        .password(password)
        .email(email)
        .role(USER)
        .build();
  }
}
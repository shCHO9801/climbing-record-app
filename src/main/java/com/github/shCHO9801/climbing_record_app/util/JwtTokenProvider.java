package com.github.shCHO9801.climbing_record_app.util;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

  private final JwtUtil jwtUtil;

  public JwtTokenProvider(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
    }
    throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
  }

  // Helper 메서드: 토큰 유효성 검사 및 사용자 ID 추출
  public String validateAndGetUserId(String token) {
    if (!jwtUtil.validateToken(token)) {
      throw new CustomException(ErrorCode.INVALID_JWT);
    }
    return jwtUtil.extractId(token);
  }

}

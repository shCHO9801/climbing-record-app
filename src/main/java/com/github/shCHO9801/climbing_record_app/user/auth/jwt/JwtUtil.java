package com.github.shCHO9801.climbing_record_app.user.auth.jwt;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.JWT_GENERATION_FAILED;

import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.LoggingUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String SECRET_KEY;
  private final long EXPIRATION_TIME;
  private final LoggingUtil loggingUtil;

  public JwtUtil(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.expiration}") long expirationTime,
      LoggingUtil loggingUtil
  ) {
    this.SECRET_KEY = secretKey;
    this.EXPIRATION_TIME = expirationTime;
    this.loggingUtil = loggingUtil;
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  public String generateToken(String id, String role) {
    try {
      return Jwts.builder()
          .setSubject(id)
          .claim("role", role)
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
          .signWith(getSigningKey(), SignatureAlgorithm.HS256)
          .compact();
    } catch (JwtException | IllegalArgumentException e) {
      loggingUtil.logError("JwtUtil", "JWT 토큰 생성 중 오류 발생: " + e.getMessage());
      throw new CustomException(JWT_GENERATION_FAILED);
    }
  }

  public String extractId(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
    } catch (ExpiredJwtException e) {
      loggingUtil.logError("JwtUtil", "만료된 JWT 토큰: " + e.getMessage());
      throw new CustomException(ErrorCode.EXPIRED_JWT);
    } catch (JwtException | IllegalArgumentException e) {
      loggingUtil.logError("JwtUtil", "유효하지 않은 JWT 토큰: " + e.getMessage());
      throw new CustomException(ErrorCode.INVALID_JWT);
    }
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      loggingUtil.logError("JwtUtil", "잘못된 JWT : " + e.getMessage());
      throw new CustomException(ErrorCode.INVALID_JWT);
    }
  }
}

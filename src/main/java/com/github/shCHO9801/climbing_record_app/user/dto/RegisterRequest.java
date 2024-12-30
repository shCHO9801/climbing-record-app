package com.github.shCHO9801.climbing_record_app.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
}

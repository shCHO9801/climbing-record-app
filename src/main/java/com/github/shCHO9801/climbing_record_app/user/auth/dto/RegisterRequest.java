package com.github.shCHO9801.climbing_record_app.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
}

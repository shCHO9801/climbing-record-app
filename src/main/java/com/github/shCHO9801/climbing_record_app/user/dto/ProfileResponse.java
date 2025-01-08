package com.github.shCHO9801.climbing_record_app.user.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {

  private String id;
  private String email;
  private String nickname;
  private Double height;
  private Double armLength;
  private Map<String, Object> equipmentInfo;
}
package com.github.shCHO9801.climbing_record_app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProfileDto {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {

    private String nickname;
    private Double armLength;
    private Double height;
    private String equipmentInfo;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    private String id;
    private String email;
    private String nickname;
    private Double height;
    private Double armLength;
    private String equipmentInfo;
  }
}

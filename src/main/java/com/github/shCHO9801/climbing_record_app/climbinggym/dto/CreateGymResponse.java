package com.github.shCHO9801.climbing_record_app.climbinggym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGymResponse {

  private Long id;
  private String name;
  private Double latitude;
  private Double longitude;
  private String createdAt;
}

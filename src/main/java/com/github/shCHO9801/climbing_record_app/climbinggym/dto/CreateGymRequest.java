package com.github.shCHO9801.climbing_record_app.climbinggym.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
public class CreateGymRequest {

  @NotBlank(message = "클라이밍장 이름은 필수 입니다.")
  private String name;

  private Double latitude;
  private Double longitude;

  @NotNull(message = "가격은 필수입니다.")
  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  private Integer price;
  private String parkingInfo;
  private List<String> difficultyChart;
  private String amenities;
}

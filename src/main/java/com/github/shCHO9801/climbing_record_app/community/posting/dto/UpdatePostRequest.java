package com.github.shCHO9801.climbing_record_app.community.posting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

  @NotBlank(message = "제목은 필수입니다.")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  private String content;
}

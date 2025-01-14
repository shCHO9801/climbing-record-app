package com.github.shCHO9801.climbing_record_app.community.posting.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {

  private String title;
  private String content;
  private Long climbingGymId;
  @Size(max = 10, message = "첨부 가능한 미디어는 최대 10개입니다.")
  private List<PostMediaRequest> media;
}

package com.github.shCHO9801.climbing_record_app.community.posting.entity;

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
public class GetPostResponse {

  private Long id;
  private String title;
  private String content;
  private String userId;
  private Long gymId;
}

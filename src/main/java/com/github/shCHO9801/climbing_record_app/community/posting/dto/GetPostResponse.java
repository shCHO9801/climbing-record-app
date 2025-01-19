package com.github.shCHO9801.climbing_record_app.community.posting.dto;

import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
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

  public static GetPostResponse getPostResponse(Post post) {
    return GetPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .userId(post.getUser().getId())
        .gymId(post.getClimbingGym().getId())
        .build();
  }
}

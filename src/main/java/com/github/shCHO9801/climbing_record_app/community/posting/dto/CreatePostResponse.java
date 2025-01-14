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
public class CreatePostResponse {
  private Long postId;
  private String message;

  public static CreatePostResponse createPostResponse(Post post, String message) {
    return CreatePostResponse.builder()
        .postId(post.getId())
        .message(message)
        .build();
  }
}

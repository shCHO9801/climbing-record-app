package com.github.shCHO9801.climbing_record_app.community.comment.dto;

import java.time.LocalDateTime;
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
public class CommentResponse {

  private Long id;
  private String content;
  private Long postId;
  private String userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

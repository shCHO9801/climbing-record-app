package com.github.shCHO9801.climbing_record_app.community.posting.dto;

import com.github.shCHO9801.climbing_record_app.community.posting.entity.MediaType;
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
public class PostMediaRequest {
  private String mediaUrl;
  private MediaType mediaType;
}

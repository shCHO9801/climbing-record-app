package com.github.shCHO9801.climbing_record_app.community.posting.controller;

import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.service.PostService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final JwtTokenProvider provider;
  private final PostService postService;

  @PostMapping
  public ResponseEntity<CreatePostResponse> createPost(
      @RequestHeader("Authorization") String authorization,
      @RequestBody CreatePostRequest request
  ) {
    String userId = extractUserId(authorization);
    Post savedPost = postService.createPost(userId, request);

    CreatePostResponse response = CreatePostResponse.builder()
        .postId(savedPost.getId())
        .message("게시글이 성공적으로 생성되었습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }
}

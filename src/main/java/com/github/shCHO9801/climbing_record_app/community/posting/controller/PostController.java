package com.github.shCHO9801.climbing_record_app.community.posting.controller;

import static com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostResponse.createPostResponse;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.UpdatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.GetPostResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.service.PostService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    CreatePostResponse response =
        createPostResponse(savedPost, "게시글이 성공적으로 생성되었습니다.");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<GetPostResponse>> getAllPosts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable =
        PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<Post> posts = postService.getAllPosts(pageable);

    PagedResponse<GetPostResponse> response = createPagedResponse(posts);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 게시글 수정
  @PutMapping("/{postId}")
  public ResponseEntity<CreatePostResponse> updatePost(
      @RequestHeader("Authorization") String authorization,
      @PathVariable Long postId,
      @RequestBody UpdatePostRequest request
  ) {
    String userId = extractUserId(authorization);
    Post savedPost = postService.updatePost(userId, postId, request);
    CreatePostResponse response =
        createPostResponse(savedPost, "게시글이 성공적으로 수정되었습니다.");

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 게시글 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @RequestHeader("Authorization") String authorization,
      @PathVariable Long postId
  ) {
    String userId = extractUserId(authorization);
    postService.deletePost(userId, postId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }

  private PagedResponse<GetPostResponse> createPagedResponse(Page<Post> posts) {
    return PagedResponse.<GetPostResponse>builder()
        .content(posts.getContent().stream()
            .map(GetPostResponse::getPostResponse)
            .toList())
        .page(posts.getNumber())
        .size(posts.getSize())
        .totalElements(posts.getTotalElements())
        .last(posts.isLast())
        .build();
  }
}
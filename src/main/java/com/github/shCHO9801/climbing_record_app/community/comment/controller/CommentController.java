package com.github.shCHO9801.climbing_record_app.community.comment.controller;

import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.CommentResponse;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.CreateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.UpdateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.entity.Comment;
import com.github.shCHO9801.climbing_record_app.community.comment.service.CommentService;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class CommentController {

  private final JwtTokenProvider provider;
  private final CommentService commentService;

  @PostMapping("/{postId}/comments")
  public ResponseEntity<CommentResponse> createComment(
      @PathVariable Long postId,
      @RequestHeader("Authorization") String authorization,
      @RequestBody CreateCommentRequest request
  ) {

    String userId = extractUserId(authorization);
    Comment comment = commentService.createComment(postId, userId, request.getContent());
    CommentResponse response = buildResponse(comment);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{postId}/comments")
  public ResponseEntity<PagedResponse<CommentResponse>> getComment(
      @PathVariable Long postId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);

    Page<Comment> comments = commentService.getComments(postId, pageable);
    PagedResponse<CommentResponse> responses = createPagedResponse(comments);

    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PutMapping("/{postId}/comments/{commentId}")
  public ResponseEntity<CommentResponse> updateComment(
      @PathVariable Long postId,
      @PathVariable Long commentId,
      @RequestHeader("Authorization") String authorization,
      @RequestBody UpdateCommentRequest request
  ) {
    String userId = extractUserId(authorization);
    Comment comment = commentService.updateComment(userId, postId, commentId, request);
    CommentResponse response = buildResponse(comment);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @DeleteMapping("/{postId}/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable Long postId,
      @PathVariable Long commentId,
      @RequestHeader("Authorization") String authorization
      ) {
      String userId = extractUserId(authorization);

      commentService.deleteComment(userId, postId, commentId);

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private PagedResponse<CommentResponse> createPagedResponse(Page<Comment> comments) {
    return PagedResponse.<CommentResponse>builder()
        .content(comments.getContent().stream()
            .map(this::buildResponse)
            .toList())
        .page(comments.getNumber())
        .size(comments.getSize())
        .totalElements(comments.getTotalElements())
        .last(comments.isLast())
        .build();
  }

  private CommentResponse buildResponse(Comment comment) {
    return CommentResponse.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .postId(comment.getPost().getId())
        .userId(comment.getUser().getId())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }

  private String extractUserId(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    String token = authorizationHeader.replace("Bearer ", "");
    return provider.validateAndGetUserId(token);
  }

}

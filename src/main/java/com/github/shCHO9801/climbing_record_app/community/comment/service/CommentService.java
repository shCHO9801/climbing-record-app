package com.github.shCHO9801.climbing_record_app.community.comment.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.POST_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.comment.dto.CreateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.UpdateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.entity.Comment;
import com.github.shCHO9801.climbing_record_app.community.comment.repository.CommentRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public Comment createComment(Long postId, String userId, CreateCommentRequest request) {

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    return commentRepository.save(buildComment(user, post, request));
  }

  public Page<Comment> getComments(Long postId, Pageable pageable) {
    return commentRepository.getCommentsByPostId(postId, pageable);
  }

  public Comment updateComment(String userId, Long commentId, UpdateCommentRequest request) {
    User user = findUser(userId);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    if(!postRepository.existsById(comment.getPost().getId())){
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    if(user.getUserNum() != comment.getUser().getUserNum()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    comment.setContent(request.getComment());
    return commentRepository.save(comment);
  }

  public void deleteComment(String userId, Long commentId) {
    User user = findUser(userId);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    if(!postRepository.existsById(comment.getPost().getId())) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    if(user.getUserNum() != comment.getUser().getUserNum()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
    }

    commentRepository.delete(comment);
  }

  private User findUser(String userId) {
    return userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  private Comment buildComment(User user, Post post, CreateCommentRequest request) {
    return Comment.builder()
        .content(request.getContent())
        .post(post)
        .user(user)
        .build();
  }
}

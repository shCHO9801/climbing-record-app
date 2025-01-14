package com.github.shCHO9801.climbing_record_app.community.comment.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.POST_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.comment.dto.UpdateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.entity.Comment;
import com.github.shCHO9801.climbing_record_app.community.comment.repository.CommentRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
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

  public Comment createComment(Long postId, String userId, String content) {

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    return commentRepository.save(Comment.buildComment(user, post, content));
  }

  public Page<Comment> getComments(Long postId, Pageable pageable) {
    return commentRepository.getCommentsByPostId(postId, pageable);
  }

  public Comment updateComment(String userId, Long postId, Long commentId,
      UpdateCommentRequest request) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    validateCommentsOwnership(userId, postId, comment);

    comment.setContent(request.getContent());
    return commentRepository.save(comment);
  }

  public void deleteComment(String userId, Long postId, Long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    validateCommentsOwnership(userId, postId, comment);

    commentRepository.delete(comment);
  }

  private void validateCommentsOwnership(String userId, Long postId, Comment comment) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    if (!Objects.equals(user.getUserNum(), comment.getUser().getUserNum())) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }
  }
}

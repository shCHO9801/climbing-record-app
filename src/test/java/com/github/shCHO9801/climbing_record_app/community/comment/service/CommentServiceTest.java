package com.github.shCHO9801.climbing_record_app.community.comment.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.POST_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.CreateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.dto.UpdateCommentRequest;
import com.github.shCHO9801.climbing_record_app.community.comment.entity.Comment;
import com.github.shCHO9801.climbing_record_app.community.comment.repository.CommentRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("댓글 유닛 테스트")
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  private User user;
  private Post post;
  private Comment comment;
  private ClimbingGym gym;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .userNum(1L)
        .id("testUser")
        .password("testPassword")
        .build();

    gym = ClimbingGym.builder()
        .id(1L)
        .name("testGym")
        .price(20000)
        .build();

    post = Post.builder()
        .id(1L)
        .title("testTitle")
        .content("testContent")
        .user(user)
        .build();

    comment = Comment.builder()
        .id(1L)
        .content("testCommentContent")
        .user(user)
        .post(post)
        .build();
  }

  @Test
  @DisplayName("댓글 생성 성공")
  void createCommentSuccess() {
    //given
    CreateCommentRequest commentRequest
        = buildCommentRequest("testCommentContent");

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(postRepository.findById(post.getId()))
        .thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class)))
        .thenReturn(comment);

    //when
    Comment create = commentService.createComment(post.getId(), user.getId(), commentRequest);

    //then
    assertNotNull(create);
    assertEquals(comment.getId(), create.getId());
    assertEquals(user.getId(), create.getUser().getId());
    assertEquals(comment.getContent(), create.getContent());
    assertEquals(commentRequest.getContent(), create.getContent());
    verify(commentRepository, times(1))
        .save(any(Comment.class));
  }

  @Test
  @DisplayName("뎃글 생성 실패 - 유저 미존재")
  void createCommentFailUserNotFound() {
    //given
    CreateCommentRequest commentRequest
        = buildCommentRequest("testCommentContent");

    when(userRepository.findByUsername("nonExistentUser"))
        .thenReturn(Optional.empty());

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> commentService.createComment(post.getId(), "nonExistentUser", commentRequest));

    //then
    assertEquals(exception.getErrorCode(), USER_NOT_FOUND);
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 생성 실패 - 게시글 미존재")
  void createCommentFailPostNotFound() {
    //given
    CreateCommentRequest commentRequest
        = buildCommentRequest("testCommentContent");

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(postRepository.findById(9999L))
        .thenReturn(Optional.empty());

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> commentService.createComment(9999L, user.getId(), commentRequest));

    //then
    assertEquals(exception.getErrorCode(), POST_NOT_FOUND);
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  @DisplayName("게시글 수정 성공")
  void updateCommentSuccess() {
    //given
    UpdateCommentRequest updateRequest =
        buildUpdateRequeat("수정된 댓글");

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(commentRepository.findById(comment.getId()))
        .thenReturn(Optional.of(comment));
    when(postRepository.existsById(post.getId()))
        .thenReturn(true);
    when(commentRepository.save(any(Comment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    //when
    Comment updated
        = commentService.updateComment(user.getId(), comment.getId(), updateRequest);

    //then
    assertNotNull(updated);
    assertEquals(comment.getId(), updated.getId());
    assertEquals(updateRequest.getComment(), updated.getContent());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 권한 없음")
  void updateCommentFailUnauthorized() {
    //given
    UpdateCommentRequest updateRequest
        = buildUpdateRequeat("수정된 댓글");

    User anotherUser = User.builder()
        .userNum(2L)
        .id("anotherUser")
        .password("anotherPassword")
        .build();

    when(commentRepository.findById(comment.getId()))
        .thenReturn(Optional.of(comment));
    when(userRepository.findByUsername(anotherUser.getId()))
        .thenReturn(Optional.of(anotherUser));
    when(postRepository.existsById(post.getId()))
        .thenReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> commentService.updateComment(anotherUser.getId(), comment.getId(), updateRequest));

    //then
    assertEquals(exception.getErrorCode(), UNAUTHORIZED_ACTION);
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 삭제 성공")
  void deleteCommentSuccess() {
    //given
    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(commentRepository.findById(comment.getId()))
        .thenReturn(Optional.of(comment));
    when(postRepository.existsById(post.getId()))
        .thenReturn(true);

    //when&then
    commentService.deleteComment(user.getId(), comment.getId());
    verify(commentRepository, times(1))
        .delete(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 삭제 실패 - 권한 없음")
  void deleteCommentFailUnauthorized() {
    //given
    User anotherUser = User.builder()
        .userNum(2L)
        .id("anotherUser")
        .password("anotherPassword")
        .build();

    when(userRepository.findByUsername(anotherUser.getId()))
        .thenReturn(Optional.of(anotherUser));
    when(commentRepository.findById(comment.getId()))
        .thenReturn(Optional.of(comment));
    when(postRepository.existsById(post.getId()))
        .thenReturn(true);

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> commentService.deleteComment(anotherUser.getId(), comment.getId()));
    assertEquals(exception.getErrorCode(), UNAUTHORIZED_ACTION);
    verify(commentRepository, never())
        .delete(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 목록 조회 성공")
  void getCommentsSuccess() {
      //given
    Pageable pageable = PageRequest.of(0, 10);
    List<Comment> commentList = Arrays.asList(comment,
        Comment.builder()
            .id(2L)
            .content("2번째")
            .createdAt(LocalDateTime.now())
            .user(user)
            .post(post)
            .build());
    Page<Comment> commentPage = new PageImpl<>(commentList, pageable, commentList.size());

    when(commentRepository.getCommentsByPostId(post.getId(), pageable))
    .thenReturn(commentPage);

      //when
    Page<Comment> result = commentService.getComments(post.getId(), pageable);

      //then
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals(comment, result.getContent().get(0));
    assertEquals("2번째", result.getContent().get(1).getContent());
  }

  private UpdateCommentRequest buildUpdateRequeat(String comment) {
    return UpdateCommentRequest.builder()
        .comment(comment)
        .build();
  }

  private CreateCommentRequest buildCommentRequest(String content) {
    return CreateCommentRequest.builder()
        .content(content)
        .build();
  }
}
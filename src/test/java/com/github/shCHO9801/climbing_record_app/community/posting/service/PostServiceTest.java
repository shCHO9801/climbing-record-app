package com.github.shCHO9801.climbing_record_app.community.posting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.POST_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.PostMediaRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.UpdatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.GetPostResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.MediaType;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.PostMedia;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostMediaRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("게시글 유닛 테스트")
class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ClimbingGymRepository climbingGymRepository;

  @Mock
  private PostMediaRepository postMediaRepository;

  private User user;
  private Post post;
  private ClimbingGym gym;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .userNum(1L)
        .id("user")
        .password("password")
        .email("test@email.com")
        .role(Role.USER)
        .createdAt(LocalDateTime.now())
        .build();

    gym = ClimbingGym.builder()
        .id(100L)
        .price(20000)
        .name("testGym")
        .createdAt(LocalDateTime.now())
        .build();

    post = Post.builder()
        .id(1L)
        .title("title")
        .content("content")
        .climbingGym(gym)
        .user(user)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("게시글 생성 성공")
  void createPostSuccess() {
    //given
    CreatePostRequest request = CreatePostRequest.builder()
        .title("title")
        .content("content")
        .climbingGymId(100L)
        .media(Arrays.asList(
            PostMediaRequest.builder()
                .mediaUrl("http://example.com/image.jpg")
                .mediaType(MediaType.IMAGE)
                .build()
        ))
        .build();

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));
    when(climbingGymRepository.findById(gym.getId()))
        .thenReturn(Optional.of(gym));
    when(postRepository.save(any(Post.class))).thenReturn(post);

    //when
    Post createdPost = postService.createPost(user.getId(), request);

    //then
    assertNotNull(createdPost);
    assertEquals(post.getTitle(), createdPost.getTitle());
    assertEquals(post.getContent(), createdPost.getContent());
    assertEquals(post.getClimbingGym(), createdPost.getClimbingGym());
    assertEquals(post.getCreatedAt(), createdPost.getCreatedAt());
    assertEquals(post.getUser().getId(), createdPost.getUser().getId());
    verify(postMediaRepository, times(1)).save(any(PostMedia.class));
  }

  @Test
  @DisplayName("게시글 생성 실패 - 유저 미존재")
  void createPostFailUserNotFound() {
    //given
    CreatePostRequest request = CreatePostRequest.builder()
        .title("title")
        .content("content")
        .climbingGymId(100L)
        .build();

    when(userRepository.findByUsername("nonExistsUserName"))
        .thenReturn(Optional.empty());

    //when&then
    assertThrows(CustomException.class,
        () -> postService.createPost("nonExistsUserName", request));
    verify(postMediaRepository, never()).save(any(PostMedia.class));
  }

  @Test
  @DisplayName("게시글 생성 실패 - 클라이밍장 미존재")
  void createPostFailClimbingGymNotFound() {
    //given
    CreatePostRequest request = CreatePostRequest.builder()
        .title("title")
        .content("content")
        .climbingGymId(5L)
        .build();

    when(userRepository.findByUsername(user.getId()))
        .thenReturn(Optional.of(user));

    when(climbingGymRepository.findById(5L))
        .thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> postService.createPost(user.getId(), request));
    assertEquals(exception.getErrorCode(), CLIMBING_GYM_NOT_FOUND);
  }

  @Test
  @DisplayName("게시글 조회 성공")
  void getPostSuccess() {
    //given
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    //when
    GetPostResponse foundPost = postService.getPost(1L);

    //then
    assertNotNull(post);
    assertEquals(foundPost.getTitle(), post.getTitle());
    assertEquals(foundPost.getContent(), post.getContent());
    assertEquals(foundPost.getGymId(), post.getClimbingGym().getId());
    assertEquals(foundPost.getUserId(), post.getUser().getId());
  }

  @Test
  @DisplayName("게시글 조회 실패 - 게시글 미존재")
  void getPostFailPostNotFound() {
    //given
    when(postRepository.findById(999L)).thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> postService.getPost(999L));

    assertEquals(exception.getErrorCode(), POST_NOT_FOUND);
  }

  @Test
  @DisplayName("게시글 목록 조회 성공")
  void getAllPostsSuccess() {
    //given
    Pageable pageable = PageRequest.of(0, 10);
    List<Post> posts = Arrays.asList(post, post);
    when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(posts));

    //when
    var pageResponse = postService.getAllPosts(pageable);

    //then
    assertNotNull(pageResponse);
    assertEquals(2, pageResponse.getTotalElements());
    assertEquals(post, pageResponse.getContent().get(0));
    assertEquals(post, pageResponse.getContent().get(1));
  }

  @Test
  @DisplayName("게시글 수정 성공")
  void updatePostSuccess() {
    //given
    UpdatePostRequest request = UpdatePostRequest.builder()
        .title("Update")
        .content("UpdateContent")
        .build();

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    Post updatePost = Post.builder()
        .id(post.getId())
        .title(request.getTitle())
        .content(request.getContent())
        .user(post.getUser())
        .climbingGym(post.getClimbingGym())
        .createdAt(post.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();
    when(postRepository.save(any(Post.class))).thenReturn(updatePost);

    //when
    Post result = postService.updatePost(user.getId(), post.getId(), request);

    //then
    assertNotNull(result);
    assertEquals(updatePost.getTitle(), result.getTitle());
    assertEquals(updatePost.getContent(), result.getContent());
    assertEquals(updatePost.getUser().getId(), result.getUser().getId());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 권한 없음")
  void updatePostFailUnauthorized() {
    //given
    UpdatePostRequest request = UpdatePostRequest.builder()
        .title("Update")
        .content("UpdateContent")
        .build();

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> postService.updatePost("anotherUser", post.getId(), request));

    assertEquals(exception.getErrorCode(), UNAUTHORIZED_ACTION);
  }

  @Test
  @DisplayName("게시글 삭제 성공")
  void deletePostSuccess() {
    //given
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    //when
    postService.deletePost(user.getId(), post.getId());

    //then
    verify(postRepository, times(1)).delete(any(Post.class));
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 권한 없음")
  void deletePostFailUnauthorized() {
    //given
    when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

    //when&then
    CustomException exception = assertThrows(CustomException.class,
        () -> postService.deletePost("anotherUser", post.getId()));

    assertEquals(exception.getErrorCode(), UNAUTHORIZED_ACTION);
    verify(postRepository, never()).delete(any(Post.class));
  }
}
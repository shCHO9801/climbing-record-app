package com.github.shCHO9801.climbing_record_app.community.posting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.POST_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.UNAUTHORIZED_ACTION;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.PostMediaRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.UpdatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.GetPostResponse;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.Post;
import com.github.shCHO9801.climbing_record_app.community.posting.entity.PostMedia;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostMediaRepository;
import com.github.shCHO9801.climbing_record_app.community.posting.repository.PostRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostMediaRepository postMediaRepository;
  private final UserRepository userRepository;
  private final ClimbingGymRepository climbingGymRepository;

  //TODO : 추후 AWS S3 업로드 서비스

  @Transactional
  public Post createPost(String userId, CreatePostRequest request) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    ClimbingGym gym = climbingGymRepository.findById(request.getClimbingGymId())
        .orElseThrow(() -> new CustomException(CLIMBING_GYM_NOT_FOUND));

    Post post = postRepository.save(buildPost(user, gym, request));

    if (request.getMedia() != null && !request.getMedia().isEmpty()) {
      request.getMedia().forEach(mediaDto -> {
        PostMedia media = buildPostMedia(post, mediaDto);
        postMediaRepository.save(media);
      });
    }

    return post;
  }

  public Page<Post> getAllPosts(Pageable pageable) {
    return postRepository.findAll(pageable);
  }

  public GetPostResponse getPost(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    return getPostResponse(post);
  }



  public Post updatePost(String userId, Long postId, UpdatePostRequest request) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    if (!post.getUser().getId().equals(userId)) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }

    Post updatePost = setUpdatePost(request, post);

    return postRepository.save(updatePost);
  }

  public void deletePost(String userId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    if (!post.getUser().getId().equals(userId)) {
      throw new CustomException(UNAUTHORIZED_ACTION);
    }

    postRepository.delete(post);
  }

  private Post setUpdatePost(UpdatePostRequest request, Post post) {
    post.setTitle(request.getTitle());
    post.setContent(request.getContent());
    post.setUpdatedAt(LocalDateTime.now());
    return post;
  }

  private PostMedia buildPostMedia(Post post, PostMediaRequest mediaDto) {
    return PostMedia.builder()
        .mediaUrl(mediaDto.getMediaUrl())
        .mediaType(mediaDto.getMediaType())
        .post(post)
        .build();
  }

  private Post buildPost(User user, ClimbingGym gym, CreatePostRequest request) {
    return Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .user(user)
        .climbingGym(gym)
        .createdAt(LocalDateTime.now())
        .build();
  }

  private GetPostResponse getPostResponse(Post post) {
    return GetPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .userId(post.getUser().getId())
        .gymId(post.getClimbingGym().getId())
        .build();
  }

}
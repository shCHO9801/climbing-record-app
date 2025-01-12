package com.github.shCHO9801.climbing_record_app.community.posting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.community.posting.dto.CreatePostRequest;
import com.github.shCHO9801.climbing_record_app.community.posting.dto.PostMediaRequest;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostMediaRepository postMediaRepository;
  private final UserRepository userRepository;

  //TODO : 추후 AWS S3 업로드 서비스

  @Transactional
  public Post createPost(String userId, CreatePostRequest request) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Post post = postRepository.save(buildPost(user, request));

    if(request.getMedia() != null && !request.getMedia().isEmpty()) {
      request.getMedia().forEach(mediaDto -> {
        PostMedia media = buildPostMedia(post, mediaDto);
        postMediaRepository.save(media);
      });
    }

    return post;
  }

  private PostMedia buildPostMedia(Post post, PostMediaRequest mediaDto) {
    return PostMedia.builder()
        .mediaUrl(mediaDto.getMediaUrl())
        .mediaType(mediaDto.getMediaType())
        .post(post)
        .build();
  }

  private Post buildPost(User user, CreatePostRequest request) {
    return Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .user(user)
        .createdAt(LocalDateTime.now())
        .build();
  }

}

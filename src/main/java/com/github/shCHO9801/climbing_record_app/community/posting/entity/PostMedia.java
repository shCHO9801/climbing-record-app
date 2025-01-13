package com.github.shCHO9801.climbing_record_app.community.posting.entity;

import com.github.shCHO9801.climbing_record_app.community.posting.dto.PostMediaRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMedia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String mediaUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaType mediaType;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  public static PostMedia buildPostMedia(Post post, PostMediaRequest request) {
    return PostMedia.builder()
        .mediaUrl(request.getMediaUrl())
        .mediaType(request.getMediaType())
        .post(post)
        .build();
  }
}

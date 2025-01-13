package com.github.shCHO9801.climbing_record_app.community.comment.repository;

import com.github.shCHO9801.climbing_record_app.community.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> getCommentsByPostId(Long postId, Pageable pageable);
}

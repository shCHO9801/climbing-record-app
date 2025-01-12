package com.github.shCHO9801.climbing_record_app.community.posting.repository;

import com.github.shCHO9801.climbing_record_app.community.posting.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {

}

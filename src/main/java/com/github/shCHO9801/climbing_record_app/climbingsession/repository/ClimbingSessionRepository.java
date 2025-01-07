package com.github.shCHO9801.climbing_record_app.climbingsession.repository;

import com.github.shCHO9801.climbing_record_app.climbingsession.entity.ClimbingSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimbingSessionRepository extends JpaRepository<ClimbingSession, Long> {

  Page<ClimbingSession> findByUser_UserNum(Long userNum, Pageable pageable);
}

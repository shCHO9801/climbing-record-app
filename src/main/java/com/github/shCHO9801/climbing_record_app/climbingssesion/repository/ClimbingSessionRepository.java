package com.github.shCHO9801.climbing_record_app.climbingssesion.repository;

import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.ClimbingSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimbingSessionRepository extends JpaRepository<ClimbingSession, Long> {

  List<ClimbingSession> findByUser_UserNum(Long userNum);
}

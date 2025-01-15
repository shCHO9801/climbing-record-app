package com.github.shCHO9801.climbing_record_app.community.meeting.repository;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Meeting> findById(Long meetingId);
}

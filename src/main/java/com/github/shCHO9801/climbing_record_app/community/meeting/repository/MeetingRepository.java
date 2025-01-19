package com.github.shCHO9801.climbing_record_app.community.meeting.repository;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Meeting> findById(Long meetingId);

  @Query("select m from Meeting m where m.id = :meetingId")
  Optional<Meeting> findByIdWithoutLock(@Param("meetingId") Long meetingId);

  @Modifying
  @Query("update Meeting m set m.participantCount = m.participantCount + 1 where m.id = :meetingId")
  int increaseParticipantCount(@Param("meetingId") Long meetingId);
}

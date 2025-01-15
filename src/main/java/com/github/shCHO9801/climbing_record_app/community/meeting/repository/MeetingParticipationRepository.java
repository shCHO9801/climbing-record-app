package com.github.shCHO9801.climbing_record_app.community.meeting.repository;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Status;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipationRepository extends JpaRepository<MeetingParticipation, Long> {

  Page<MeetingParticipation> getMeetingParticipationByMeetingId(Long meetingId, Pageable pageable);

  Page<MeetingParticipation> findAllByMeetingIdAndStatusNot(Long meetingId, Status status, Pageable pageable);

  Optional<MeetingParticipation> findByMeetingIdAndUser_Id(Long meetingId, String userId);
}

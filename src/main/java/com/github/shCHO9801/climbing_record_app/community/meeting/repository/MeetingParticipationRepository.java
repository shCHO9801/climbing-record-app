package com.github.shCHO9801.climbing_record_app.community.meeting.repository;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.MeetingParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipationRepository extends JpaRepository<MeetingParticipation, Long> {

  Page<MeetingParticipation> getMeetingParticipationByMeetingId(Long meetingId, Pageable pageable);

  Page<MeetingParticipation> findAllByMeetingId(Long meetingId, Pageable pageable);
}

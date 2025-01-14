package com.github.shCHO9801.climbing_record_app.community.meetup.repository;

import com.github.shCHO9801.climbing_record_app.community.meetup.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

}

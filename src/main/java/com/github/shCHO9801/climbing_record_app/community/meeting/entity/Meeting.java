package com.github.shCHO9801.climbing_record_app.community.meeting.entity;

import com.github.shCHO9801.climbing_record_app.community.meeting.dto.CreateMeetingRequest;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.exception.ErrorCode;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meetings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false)
  private int participantCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_num", nullable = false)
  private User host;

  @Version
  private Integer version;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static Meeting buildMeeting(User user, CreateMeetingRequest request) {
    return Meeting.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .date(request.getDate())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .capacity(request.getCapacity())
        .host(user)
        .build();
  }

  @PrePersist
  protected void onCreate() {
    validateTimes();
    this.createdAt = LocalDateTime.now();
    participantCount = 0;
  }

  @PreUpdate
  protected void onUpdate() {
    validateTimes();
    this.updatedAt = LocalDateTime.now();
  }

  private void validateTimes() {
    if (startTime == null || endTime == null) {
      throw new CustomException(ErrorCode.MEETING_TIME_NULL);
    }
    if (!startTime.isBefore(endTime)) {
      throw new CustomException(ErrorCode.MEETING_TIME_INVALID);
    }
  }
}

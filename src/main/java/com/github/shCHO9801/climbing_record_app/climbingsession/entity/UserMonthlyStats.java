package com.github.shCHO9801.climbing_record_app.climbingsession.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_monthly_stats", uniqueConstraints = @UniqueConstraint(columnNames = {"user_num",
    "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMonthlyStats {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_num", nullable = false)
  private Long userNum;

  @Column(name = "date", nullable = false)
  private String yearMonth;

  @Column(name = "total_duration")
  private int totalDuration;

}

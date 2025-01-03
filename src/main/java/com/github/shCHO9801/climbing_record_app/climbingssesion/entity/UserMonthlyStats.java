package com.github.shCHO9801.climbing_record_app.climbingssesion.entity;

import com.github.shCHO9801.climbing_record_app.converter.YearMonthConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_monthly_stats", uniqueConstraints = @UniqueConstraint(columnNames = {"user_num", "year_month"}))
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

  @Column(name = "year_m", nullable = false)
  @Convert(converter = YearMonthConverter.class)
  private YearMonth yearMonth;

  @Column(name = "total_duration")
  private int totalDuration;

}

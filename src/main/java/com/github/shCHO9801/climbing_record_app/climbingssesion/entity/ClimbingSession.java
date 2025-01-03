package com.github.shCHO9801.climbing_record_app.climbingssesion.entity;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.converter.JsonListConverter;
import com.github.shCHO9801.climbing_record_app.converter.JsonMapConverter;
import com.github.shCHO9801.climbing_record_app.converter.YearMonthConverter;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "climbing_sessions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClimbingSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "dt", nullable = false)
  @Convert(converter = YearMonthConverter.class)
  private YearMonth date;

  private int duration;

  @Column(name = "difficulty_levels_completed", columnDefinition = "JSON")
  @Convert(converter = JsonMapConverter.class)
  private Map<String, Integer> difficultyLevelsCompleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_num", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "climbing_gym_id", nullable = false)
  private ClimbingGym climbingGym;

  public void setUpDifficultyLevels(List<String> difficultyChart) {
    if (this.difficultyLevelsCompleted == null) {
      this.difficultyLevelsCompleted = new HashMap<>();
      for(String level : difficultyChart) {
        this.difficultyLevelsCompleted.put(level, 0);
      }
    }
  }
}

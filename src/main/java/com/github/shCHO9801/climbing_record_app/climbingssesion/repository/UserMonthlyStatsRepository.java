package com.github.shCHO9801.climbing_record_app.climbingssesion.repository;

import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.UserMonthlyStats;
import java.time.YearMonth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMonthlyStatsRepository extends JpaRepository<UserMonthlyStats, Long> {
  Optional<UserMonthlyStats> findByUserNumAndYearMonth(Long userNum, YearMonth yearMonth);

}

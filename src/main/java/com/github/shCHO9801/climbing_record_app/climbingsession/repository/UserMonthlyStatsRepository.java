package com.github.shCHO9801.climbing_record_app.climbingsession.repository;

import com.github.shCHO9801.climbing_record_app.climbingsession.entity.UserMonthlyStats;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMonthlyStatsRepository extends JpaRepository<UserMonthlyStats, Long> {

  Optional<UserMonthlyStats> findByUserNumAndYearMonth(Long userNum, String yearMonth);

  Page<UserMonthlyStats> findByUserNum(Long userNum, Pageable pageable);

  Page<UserMonthlyStats> findByUserNumAndYearMonthStartingWith(Long userNum, String year, Pageable pageable);
}

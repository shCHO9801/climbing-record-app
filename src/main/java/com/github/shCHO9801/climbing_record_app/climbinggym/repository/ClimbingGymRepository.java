package com.github.shCHO9801.climbing_record_app.climbinggym.repository;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimbingGymRepository extends JpaRepository<ClimbingGym, Long> {

  boolean existsByName(String name);
}

package com.github.shCHO9801.climbing_record_app.user.repository;

import com.github.shCHO9801.climbing_record_app.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUserNum(Long userNum);

  @Query("select u from User u where u.id = :username")
  Optional<User> findByUsername(@Param("username") String username);
}

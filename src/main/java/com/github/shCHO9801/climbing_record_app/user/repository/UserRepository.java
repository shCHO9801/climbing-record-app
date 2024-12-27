package com.github.shCHO9801.climbing_record_app.user.repository;

import com.github.shCHO9801.climbing_record_app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}

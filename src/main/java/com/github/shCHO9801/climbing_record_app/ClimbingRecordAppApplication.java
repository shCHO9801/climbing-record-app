package com.github.shCHO9801.climbing_record_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ClimbingRecordAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClimbingRecordAppApplication.class, args);
  }

}

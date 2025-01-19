package com.github.shCHO9801.climbing_record_app.community.meeting.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.MEETING_CAPACITY_EXCEEDED;

import com.github.shCHO9801.climbing_record_app.community.meeting.entity.Meeting;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingParticipationRepository;
import com.github.shCHO9801.climbing_record_app.community.meeting.repository.MeetingRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("동시성 테스트")
public class MeetingParticipationConcurrencyTest {

  private static final Logger logger = LoggerFactory.getLogger(
      MeetingParticipationConcurrencyTest.class);

  @Autowired
  private MeetingParticipationService meetingParticipationService;

  @Autowired
  private MeetingRepository meetingRepository;

  @Autowired
  private MeetingParticipationRepository meetingParticipationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MeetingParticipationHelperService meetingParticipationHelperService;

  private Meeting meeting;
  private User hostUser;
  private User joiningUser;

  // 테스트 시작 시 사용할 CountDownLatch (모든 스레드가 대기할 latch)
  private CountDownLatch latch;

  @BeforeEach
  void setUp() {
    hostUser = userRepository.findByUsername("host").orElseGet(() -> {
      User newUser = User.builder()
          .id("host")
          .password("password")
          .email("host@example.com")
          .role(Role.USER)
          .build();
      return userRepository.save(newUser);
    });

    joiningUser = userRepository.findByUsername("joiner").orElseGet(() -> {
      User newUser = User.builder()
          .id("joiner")
          .password("password")
          .email("joiner@example.com")
          .role(Role.USER)
          .build();
      return userRepository.save(newUser);
    });

    meeting = Meeting.builder()
        .title("Concurrency Meeting Test")
        .description("Test meeting for concurrency")
        .date(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(12, 0))
        .capacity(5)
        .host(hostUser)
        .build();
    meeting = meetingRepository.save(meeting);

    logger.info("테스트 준비 완료: meetingId={}, capacity={}", meeting.getId(), meeting.getCapacity());

    latch = new CountDownLatch(1);
  }

  @AfterEach
  void tearDown() {
    meetingParticipationRepository.deleteAll();
    meetingRepository.deleteAll();
    userRepository.deleteAll();
    logger.info("테스트 종료: 모든 엔티티 삭제 완료");
  }

  @Test
  void testConcurrentParticipation() throws Exception {
    int numberOfThreads = 5; // 동시 참여 요청 수 (meeting capacity에 맞게 3)
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    List<Callable<Boolean>> tasks = new ArrayList<>();

    // 각 스레드 태스크 생성: latch.await()에 의해 모두 대기한 후 참여 요청 실행
    for (int i = 0; i < numberOfThreads; i++) {
      final int threadNum = i + 1;
      // 각 스레드마다 고유한 userId 생성 (예: "joiner" + threadNum)
      tasks.add(() -> {
        String currentUserId = "joiner" + threadNum;
        // 만약 이 user가 존재하지 않으면 미리 생성
        if (!userRepository.findByUsername(currentUserId).isPresent()) {
          User newUser = User.builder()
              .id(currentUserId)
              .password("password")
              .email("joiner" + threadNum + "@example.com")
              .role(Role.USER)
              .build();
          userRepository.save(newUser);
        }
        logger.info("스레드 {}: 대기 시작", threadNum);
        latch.await();
        logger.info("스레드 {}: 대기 해제, 참여 요청 실행", threadNum);
        try {
          meetingParticipationService.participation(currentUserId, meeting.getId());
          logger.info("스레드 {}: 참여 성공", threadNum);
          return true;
        } catch (CustomException e) {
          logger.info("스레드 {}: 참여 실패, 오류코드: {}", threadNum, e.getErrorCode());
          if (e.getErrorCode().equals(MEETING_CAPACITY_EXCEEDED)) {
            return false;
          }
          throw e;
        }
      });
    }

    // 태스크들을 미리 ExecutorService에 제출
    List<Future<Boolean>> futures = new ArrayList<>();
    for (Callable<Boolean> task : tasks) {
      futures.add(executorService.submit(task));
    }

    // 모든 태스크가 제출된 후 latch를 해제 (모든 스레드 대기 해제)
    latch.countDown();
    logger.info("latch.countDown() 호출: 모든 스레드 대기 해제");

    executorService.shutdown();
    if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
      executorService.shutdownNow();
    }
    int successCount = 0;
    int failureCount = 0;
    for (Future<Boolean> future : futures) {
      if (future.get()) {
        successCount++;
      } else {
        failureCount++;
      }
    }

    logger.info("테스트 완료: 성공 참여 건수 = {}, 실패 참여 건수 = {}", successCount, failureCount);

    // meeting capacity는 3이므로, 성공한 참여 건수는 최대 3건이어야 합니다.
    Assertions.assertTrue(successCount <= meeting.getCapacity(),
        "성공한 참여 건수가 meeting capacity를 초과하였습니다.");

    // 이후 DB에서 실제 참여 건수를 확인
    Meeting updatedMeeting = meetingParticipationHelperService.loadMeetingForUpdate(
        meeting.getId());
    logger.info("DB 참여 건수: {}", updatedMeeting.getParticipantCount());
    Assertions.assertEquals(successCount, updatedMeeting.getParticipantCount(),
        "DB에 저장된 참여자 수와 성공한 참여 건수가 일치하지 않습니다.");
  }

}


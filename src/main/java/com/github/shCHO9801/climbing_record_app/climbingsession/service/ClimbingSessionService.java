package com.github.shCHO9801.climbing_record_app.climbingsession.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionRequest;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.CreateSessionResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.dto.PagedResponse;
import com.github.shCHO9801.climbing_record_app.climbingsession.entity.ClimbingSession;
import com.github.shCHO9801.climbing_record_app.climbingsession.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimbingSessionService {

  private final ClimbingSessionRepository climbingSessionRepository;
  private final ClimbingGymRepository climbingGymRepository;
  private final UserRepository userRepository;
  private final UserMonthlyStatsService monthlyStatsService;

  public CreateSessionResponse createClimbingSession(String userId, CreateSessionRequest request) {
    ClimbingGym gym = climbingGymRepository.findById(request.getClimbingGymId())
        .orElseThrow(() -> new CustomException(CLIMBING_GYM_NOT_FOUND));

    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    ClimbingSession session = createSession(gym, user, request);
    ClimbingSession saved = climbingSessionRepository.save(session);

    monthlyStatsService.aggregateUserMonthlyStats(
        saved.getUser().getUserNum(),
        saved.getDate(),
        saved.getDuration());

    return createResponse(saved);
  }

  public PagedResponse<CreateSessionResponse> getAllClimbingSessions(String userId,
      Pageable pageable) {
    User user = userRepository.findByUsername(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Long userNum = user.getUserNum();

    Page<ClimbingSession> page = climbingSessionRepository.findByUser_UserNum(userNum, pageable);
    List<CreateSessionResponse> content = page.stream()
        .map(this::mapToResponse)
        .toList();

    return PagedResponse.<CreateSessionResponse>builder()
        .content(content)
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }

  private ClimbingSession createSession(ClimbingGym gym, User user, CreateSessionRequest request) {
    ClimbingSession session = ClimbingSession.builder()
        .date(request.getDate())
        .duration(request.getDuration())
        .difficultyLevelsCompleted(request.getDifficultyLevels() != null ?
            request.getDifficultyLevels() : new HashMap<>())
        .user(user)
        .climbingGym(gym)
        .build();
    session.setUpDifficultyLevels(gym.getDifficultyChart());
    return session;
  }

  private CreateSessionResponse createResponse(ClimbingSession session) {
    return CreateSessionResponse.builder()
        .id(session.getId())
        .date(session.getDate())
        .duration(session.getDuration())
        .difficultyLevels(session.getDifficultyLevelsCompleted())
        .userId(session.getUser().getUserNum())
        .climbingGymId(session.getClimbingGym().getId())
        .climbingGymName(session.getClimbingGym().getName())
        .build();
  }

  private CreateSessionResponse mapToResponse(ClimbingSession session) {
    return CreateSessionResponse.builder()
        .id(session.getId())
        .date(session.getDate())
        .duration(session.getDuration())
        .difficultyLevels(session.getDifficultyLevelsCompleted())
        .userId(session.getUser().getUserNum())
        .climbingGymId(session.getClimbingGym().getId())
        .climbingGymName(session.getClimbingGym().getName())
        .build();
  }
}

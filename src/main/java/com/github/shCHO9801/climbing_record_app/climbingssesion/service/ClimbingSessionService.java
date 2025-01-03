package com.github.shCHO9801.climbing_record_app.climbingssesion.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_NOT_FOUND;
import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.USER_NOT_FOUND;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession;
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession.Request;
import com.github.shCHO9801.climbing_record_app.climbingssesion.dto.CreateSession.Response;
import com.github.shCHO9801.climbing_record_app.climbingssesion.entity.ClimbingSession;
import com.github.shCHO9801.climbing_record_app.climbingssesion.repository.ClimbingSessionRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import com.github.shCHO9801.climbing_record_app.user.repository.UserRepository;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimbingSessionService {

  private final ClimbingSessionRepository climbingSessionRepository;
  private final ClimbingGymRepository climbingGymRepository;
  private final UserRepository userRepository;
  private final UserMonthlyStatsService monthlyStatsService;

  public Response createClimbingSession(Request request) {
    ClimbingGym gym = climbingGymRepository.findById(request.getClimbingGymId())
        .orElseThrow(() -> new CustomException(CLIMBING_GYM_NOT_FOUND));

    User user = userRepository.findByUserNum(request.getUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    ClimbingSession session = createSession(gym, user, request);
    ClimbingSession saved = climbingSessionRepository.save(session);

    YearMonth sessionMonth = YearMonth.from(saved.getDate());
    monthlyStatsService.aggregateUserMonthlyStats(
        saved.getUser().getUserNum(),
        sessionMonth,
        saved.getDuration());

    return createResponse(saved);
  }

  public List<Response> getAllClimbingSessions(Long userNum) {
    return climbingSessionRepository.findByUser_UserNum(userNum).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  private ClimbingSession createSession(ClimbingGym gym, User user, Request request) {
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

  private CreateSession.Response createResponse(ClimbingSession session) {
    return CreateSession.Response.builder()
        .id(session.getId())
        .date(session.getDate())
        .duration(session.getDuration())
        .difficultyLevels(session.getDifficultyLevelsCompleted())
        .userId(session.getUser().getUserNum())
        .climbingGymId(session.getClimbingGym().getId())
        .climbingGymName(session.getClimbingGym().getName())
        .build();
  }

  private Response mapToResponse(ClimbingSession session) {
    return Response.builder()
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

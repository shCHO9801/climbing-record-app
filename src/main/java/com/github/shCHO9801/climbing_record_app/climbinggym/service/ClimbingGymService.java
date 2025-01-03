package com.github.shCHO9801.climbing_record_app.climbinggym.service;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.CLIMBING_GYM_ALREADY_EXISTS;

import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym.Request;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.CreateGym.Response;
import com.github.shCHO9801.climbing_record_app.climbinggym.dto.GetGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import com.github.shCHO9801.climbing_record_app.climbinggym.repository.ClimbingGymRepository;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimbingGymService {

  private final ClimbingGymRepository climbingGymRepository;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public CreateGym.Response createClimbingGym(CreateGym.Request request) {
    if (climbingGymRepository.existsByName(request.getName())) {
      throw new CustomException(CLIMBING_GYM_ALREADY_EXISTS);
    }

    ClimbingGym climbingGym = makeGym(request);

    ClimbingGym savedGym = climbingGymRepository.save(climbingGym);

    return returnResponse(savedGym);
  }

  public List<GetGym> getAllGyms() {
    return climbingGymRepository.findAll().stream()
        .map(gym -> GetGym.builder()
            .id(gym.getId())
            .name(gym.getName())
            .location(gym.getLocation())
            .price(gym.getPrice())
            .parkingInfo(gym.getParkingInfo())
            .difficultyChart(gym.getDifficultyChart())
            .amenities(gym.getAmenities())
            .createdAt(gym.getCreatedAt().format(formatter))
            .updatedAt(gym.getUpdatedAt().format(formatter))
            .build())
        .collect(Collectors.toList());
  }

  private ClimbingGym makeGym(Request request) {
    return ClimbingGym.builder()
        .name(request.getName())
        .location(request.getLocation())
        .price(request.getPrice())
        .parkingInfo(request.getParkingInfo())
        .difficultyChart(request.getDifficultyChart())
        .amenities(request.getAmenities())
        .build();
  }

  private Response returnResponse(ClimbingGym savedGym) {
    return CreateGym.Response.builder()
        .id(savedGym.getId())
        .name(savedGym.getName())
        .location(savedGym.getLocation())
        .createdAt(savedGym.getCreatedAt().format(formatter))
        .build();
  }
}

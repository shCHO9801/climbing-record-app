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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimbingGymService {

  private final ClimbingGymRepository climbingGymRepository;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  public CreateGym.Response createClimbingGym(CreateGym.Request request) {
    if (climbingGymRepository.existsByName(request.getName())) {
      throw new CustomException(CLIMBING_GYM_ALREADY_EXISTS);
    }

    ClimbingGym climbingGym = makeGym(request);

    ClimbingGym savedGym = climbingGymRepository.save(climbingGym);

    return returnResponse(savedGym);
  }

  public Page<GetGym> getAllGyms(Pageable pageable) {
    Page<ClimbingGym> gymsPage = climbingGymRepository.findAll(pageable);
    return gymsPage.map(this::convertToGym);
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

  private GetGym convertToGym(ClimbingGym savedGym) {
    return GetGym.from(savedGym);
  }
}

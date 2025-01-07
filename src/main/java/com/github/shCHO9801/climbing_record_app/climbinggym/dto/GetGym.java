package com.github.shCHO9801.climbing_record_app.climbinggym.dto;

import com.github.shCHO9801.climbing_record_app.climbinggym.entity.ClimbingGym;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGym {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  private Long id;
  private String name;
  private String location;
  private Integer price;
  private String parkingInfo;
  private List<String> difficultyChart;
  private String amenities;
  private String createdAt;
  private String updatedAt;

  public static GetGym from(ClimbingGym gym) {
    return GetGym.builder()
        .id(gym.getId())
        .name(gym.getName())
        .location(gym.getLocation().toText())
        .price(gym.getPrice())
        .parkingInfo(gym.getParkingInfo())
        .amenities(gym.getAmenities())
        .difficultyChart(gym.getDifficultyChart())
        .createdAt(gym.getCreatedAt().format(formatter))
        .build();
  }
}

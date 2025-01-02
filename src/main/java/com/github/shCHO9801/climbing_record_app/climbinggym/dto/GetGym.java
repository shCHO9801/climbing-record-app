package com.github.shCHO9801.climbing_record_app.climbinggym.dto;

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

  private Long id;
  private String name;
  private Point location;
  private Integer price;
  private String parkingInfo;
  private List<String> difficultyChart;
  private String amenities;
  private String createdAt;
  private String updatedAt;
}

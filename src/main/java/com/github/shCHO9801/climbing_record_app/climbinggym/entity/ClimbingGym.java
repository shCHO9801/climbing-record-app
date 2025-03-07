package com.github.shCHO9801.climbing_record_app.climbinggym.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "climbing_gym")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClimbingGym {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "POINT")
  private Point location;

  @Column(nullable = false)
  private Integer price;

  private String parkingInfo;

  @Type(JsonType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "difficulty_chart", columnDefinition = "JSON")
  private List<String> difficultyChart;

  private String amenities;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = true)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = null;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}

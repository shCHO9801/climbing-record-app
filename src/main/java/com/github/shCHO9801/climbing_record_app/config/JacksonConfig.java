package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  private static final String TIME_FORMAT = "kk:mm:ss";
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      SimpleModule localTimeModule = new SimpleModule();
      localTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
      localTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));

      SimpleModule geoModule = new SimpleModule();
      geoModule.addSerializer(Point.class, new PointSerializer());
      geoModule.addDeserializer(Point.class, new PointDeserializer());

      JavaTimeModule javaTimeModule = new JavaTimeModule();

      builder.modules(javaTimeModule, localTimeModule, geoModule);
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    };
  }
}

package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      SimpleModule geoModule = new SimpleModule();
      geoModule.addSerializer(org.locationtech.jts.geom.Point.class, new PointSerializer());
      geoModule.addDeserializer(org.locationtech.jts.geom.Point.class, new PointDeserializer());

      JavaTimeModule javaTimeModule = new JavaTimeModule();

      builder.modules(geoModule, javaTimeModule);
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    };
  }
}

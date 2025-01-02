package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      SimpleModule module = new SimpleModule();
      module.addSerializer(org.locationtech.jts.geom.Point.class, new PointSerializer());
      module.addDeserializer(org.locationtech.jts.geom.Point.class, new PointDeserializer());
      builder.modules(module);
    };
  }
}

package com.github.shCHO9801.climbing_record_app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI climbingRecordAppOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Climbing Record API")
            .description("클라이밍 기록 애플리케이션 API 문서")
            .version("v1.0.0"));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("climbing-record-public")
        .pathsToMatch("/api/**")
        .build();
  }
}

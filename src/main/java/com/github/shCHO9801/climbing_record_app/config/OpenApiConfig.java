package com.github.shCHO9801.climbing_record_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI climbingRecordAppOpenAPI() {
    Info info  = new Info()
        .title("Climbing Record API")
        .description("클라이밍 기록 애플리케이션 API 문서")
        .version("v1.1.0");

    String jwtSchemeName = "jwtAuth";
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
    Components components = new Components().addSecuritySchemes(jwtSchemeName,
        new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT")
    );

    return new OpenAPI()
        .components(new Components())
        .info(info)
        .addSecurityItem(securityRequirement)
        .components(components);
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("climbing-record-public")
        .pathsToMatch("/api/**")
        .build();
  }
}

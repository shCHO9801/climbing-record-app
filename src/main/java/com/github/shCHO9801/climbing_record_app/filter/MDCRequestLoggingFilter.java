package com.github.shCHO9801.climbing_record_app.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCRequestLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID = "request_id";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String requestId = request.getHeader("X-Request-ID");
      if (requestId == null || requestId.isEmpty()) {
        requestId = UUID.randomUUID().toString().replace("-", "");
      }
      MDC.put(REQUEST_ID, requestId);
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(REQUEST_ID);
    }
  }
}

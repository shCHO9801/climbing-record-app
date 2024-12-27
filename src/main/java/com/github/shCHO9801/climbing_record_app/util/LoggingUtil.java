package com.github.shCHO9801.climbing_record_app.util;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingUtil {

  public void logRequest(String action, Object... args) {
    log.info("[{}] 요청 - {}", action, Arrays.toString(args));
  }

  public void logSuccess(String action, Object result) {
    log.info("[{}] 성공 - {}", action, result);
  }

  public void logError(String action, String result) {
    log.error("[{}] 에러 - {}", action, result);
  }
}

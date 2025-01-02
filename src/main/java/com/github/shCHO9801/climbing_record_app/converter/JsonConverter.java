package com.github.shCHO9801.climbing_record_app.converter;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.JSON_CONVERT_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class JsonConverter implements AttributeConverter<List<String>, String> {

  private final ObjectMapper mapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

  @Override
  public String convertToDatabaseColumn(List<String> list) {
    try {
      return mapper.writeValueAsString(list);
    } catch (JsonProcessingException e) {
      logger.error("JsonConverter : JSON 변환 오류 = {}", e.getMessage());
      throw new CustomException(JSON_CONVERT_ERROR);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String s) {
    try {
      return mapper.readValue(s, new TypeReference<List<String>>() {});
    } catch (IOException e) {
      logger.error("JsonConverter : JSON 변환 오류 = {}", e.getMessage());
      throw new CustomException(JSON_CONVERT_ERROR);
    }
  }
}

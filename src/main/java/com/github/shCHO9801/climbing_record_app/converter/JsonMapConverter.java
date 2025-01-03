package com.github.shCHO9801.climbing_record_app.converter;

import static com.github.shCHO9801.climbing_record_app.exception.ErrorCode.JSON_CONVERT_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shCHO9801.climbing_record_app.exception.CustomException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;

@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Integer>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<String, Integer> stringIntegerMap) {
    try {
      return objectMapper.writeValueAsString(stringIntegerMap);
    } catch (JsonProcessingException e) {
      throw new CustomException(JSON_CONVERT_ERROR);
    }
  }

  @Override
  public Map<String, Integer> convertToEntityAttribute(String s) {
    try {
      return objectMapper.readValue(s, new TypeReference<Map<String, Integer>>() {
      });
    } catch (JsonProcessingException e) {
      throw new CustomException(JSON_CONVERT_ERROR);
    }
  }
}

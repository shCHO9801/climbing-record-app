package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeDeserializer extends StdDeserializer<LocalTime> {

  private final DateTimeFormatter formatter;

  protected LocalTimeDeserializer(DateTimeFormatter formatter) {
    super(LocalTime.class);
    this.formatter = formatter;
  }

  @Override
  public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    String timeStr = jsonParser.getText();
    return LocalTime.parse(timeStr, formatter);
  }
}

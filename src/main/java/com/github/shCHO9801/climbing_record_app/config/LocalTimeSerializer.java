package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

  private final DateTimeFormatter formatter;

  public LocalTimeSerializer(DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void serialize(LocalTime localTime, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(formatter.format(localTime));
  }
}

package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.locationtech.jts.geom.Point;

public class PointSerializer extends StdSerializer<Point> {

  public PointSerializer() {
    this(null);
  }

  public PointSerializer(Class<Point> t) {
    super(t);
  }

  @Override
  public void serialize(Point point, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("type", "point");
      jsonGenerator.writeArrayFieldStart("coordinates");
      jsonGenerator.writeNumber(point.getX());
      jsonGenerator.writeNumber(point.getY());
      jsonGenerator.writeEndArray();
      jsonGenerator.writeEndObject();
  }
}

package com.github.shCHO9801.climbing_record_app.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PointDeserializer extends StdDeserializer<Point> {

  public PointDeserializer() {
    this(null);
  }

  public PointDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    double x = node.get("coordinates").get(0).asDouble();
    double y = node.get("coordinates").get(1).asDouble();
    GeometryFactory geometryFactory = new GeometryFactory();
    return geometryFactory.createPoint(new Coordinate(x, y));
  }

}

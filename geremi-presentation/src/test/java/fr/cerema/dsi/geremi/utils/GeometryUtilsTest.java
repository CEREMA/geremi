package fr.cerema.dsi.geremi.utils;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.wololo.geojson.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeometryUtilsTest {

  @Test
  void testConvertJtsGeometryToGeoJson() {
    GeometryFactory geometryFactory = new GeometryFactory();
    Geometry geometry = geometryFactory.createPoint(new Coordinate(1, 2));
    Point result = (Point) GeometryUtils.convertJtsGeometryToGeoJson(geometry);
    assertNotNull(result);
    assertEquals(Point.class, result.getClass());
  }

  @Test
  void testConvertGeoJsonToJtsGeometry() {
    Point geoJson = new Point(new double[] { 1, 2 });
    Geometry result = GeometryUtils.convertGeoJsonToJtsGeometry(geoJson);
    assertNotNull(result);
    assertEquals(org.locationtech.jts.geom.Point.class, result.getClass());
  }
}

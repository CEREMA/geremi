package fr.cerema.dsi.geremi.utils;

import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

public class GeometryUtils {

  public static org.wololo.geojson.Geometry convertJtsGeometryToGeoJson(Geometry geometry) {
    return new GeoJSONWriter().write(geometry);
  }

  public static Geometry convertGeoJsonToJtsGeometry(org.wololo.geojson.Geometry geoJson) {
    return new GeoJSONReader().read(geoJson);
  }

}

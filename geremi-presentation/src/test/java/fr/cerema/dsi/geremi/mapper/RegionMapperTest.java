package fr.cerema.dsi.geremi.mapper;

import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class RegionMapperTest {


  @Test
  void testToFeature() {

    RegionDTO entity = new RegionDTO();
    entity.setId(1L);
    entity.setCode("code");
    entity.setNom("nom");
    GeometryFactory geometryFactory = new GeometryFactory();
    entity.setGeometry(geometryFactory.createPoint(new Coordinate(1, 2)));

    RegionMapper mapper = new RegionMapper();
    Feature feature = mapper.toFeature(entity);

    assertNotNull(feature);
    assertEquals(1L, feature.getId());
    assertNotNull(feature.getGeometry());
    assertEquals(Point.class, feature.getGeometry().getClass());
    Point point = (Point) feature.getGeometry();
    assertEquals(1, point.getCoordinates()[0], 1.0);
    assertEquals(2, point.getCoordinates()[1], 2.0);
    assertNotNull(feature.getProperties());
    assertEquals("code", feature.getProperties().get("code"));
    assertEquals("nom", feature.getProperties().get("nom"));
  }


}

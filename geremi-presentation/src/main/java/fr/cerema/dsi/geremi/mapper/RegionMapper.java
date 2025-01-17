package fr.cerema.dsi.geremi.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public class RegionMapper {

  public Feature toFeature(RegionDTO entity) {
    Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getGeometry());
    Long id = entity.getId();
    Map<String, Object> properties = new HashMap<>();
    properties.put("code",entity.getCode());
    properties.put("nom",entity.getNom());
    log.debug("Converting Regio to Feature: {}", entity.toString());
    return new Feature(id, geometry, properties);
  }
}

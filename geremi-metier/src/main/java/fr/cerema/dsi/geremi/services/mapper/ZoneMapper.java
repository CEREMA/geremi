package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.services.dto.ZoneDTO;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public abstract class ZoneMapper {
  public Feature toFeature(Zone entity) {
    Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getTheGeom());
    Long id = entity.getId();
    Map<String, Object> properties = new HashMap<>();
    properties.put("type", entity.getType());
    properties.put("nom", entity.getNom());
    properties.put("code", entity.getCode());
    return new Feature(id, geometry, properties);
  }

  public Feature toFeature(ZoneDTO entity) {
    Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getGeometry());
    Long id = entity.getIdZone();
    Map<String, Object> properties = new HashMap<>();
    properties.put("type", entity.getType());
    properties.put("nom", entity.getNom());
    properties.put("code", entity.getCode());
    properties.put("exterieur",entity.getExterieur());
    return new Feature(id, geometry, properties);
  }

  @Mapping(target = "idZone", source = "id")
  @Mapping(target = "idEtude", source = "etude.id")
  @Mapping(target = "geometry", ignore = true)
  public abstract ZoneDTO toDto(Zone zone);
}

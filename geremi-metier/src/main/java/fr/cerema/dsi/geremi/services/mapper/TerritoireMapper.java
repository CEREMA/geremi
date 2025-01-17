package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.services.dto.TerritoireDto;
import fr.cerema.dsi.geremi.utils.GeometryUtils;

@Mapper(componentModel = "spring", uses = {})
public abstract class TerritoireMapper {

	public Feature toFeature(Territoire entity) {
        Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getGeometry());
        Long id = entity.getId();
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", entity.getType());
        properties.put("nom", entity.getNom());
        properties.put("description", entity.getDescription());
        properties.put("liste_territoire", entity.getListeTerritoire());
        return new Feature(id, geometry, properties);
    }


    @Mapping(target = "idTerritoire", source = "id")
    //@Mapping(target = "geometry", ignore = true) //renseigné en @AfterMapping
    public abstract TerritoireDto toDto(Territoire entity);


}

package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;
import fr.cerema.dsi.geremi.services.dto.ContrainteEnvironnementaleDTO;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public abstract class ContrainteEnvironnementaleMapper {


	public Feature toFeature(ContrainteEnvironnementale entity) {
		Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getTheGeom());
		Integer id = entity.getIdContrEnv();
		Map<String, Object> properties = new HashMap<>();
		properties.put("nom", entity.getNom());
		properties.put("description", entity.getDescription());
		properties.put("niveau", entity.getNiveau());
		properties.put("id_source", entity.getIdSource());
		properties.put("id_etude", entity.getEtude().getId());
		ContrainteEnvironnementaleMapper.log.debug("Converting ContrainteEnvironnementale to Feature: {}",
				entity.toString());
		return new Feature(id, geometry, properties);
	}

	public Feature toFeature(ContrainteEnvironnementaleDTO dto) {
		Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(dto.getTheGeom());
		Integer id = dto.getIdContrEnv();
		Map<String, Object> properties = new HashMap<>();
		properties.put("nom", dto.getNom());
		properties.put("description", dto.getDescription());
		properties.put("niveau", dto.getNiveau());
		properties.put("id_source", dto.getIdSource());
		properties.put("id_etude", dto.getEtudeDTO().getId());
		ContrainteEnvironnementaleMapper.log.debug("Converting ContrainteEnvironnementaleDTO to Feature: {}",
				dto.toString());
		return new Feature(id, geometry, properties);
	}

	public List<Feature> contraintesToFeature(List<ContrainteEnvironnementale> entities) {
	    return entities.stream()
	        .map(this::toFeature)
	        .collect(Collectors.toList());
	}

}

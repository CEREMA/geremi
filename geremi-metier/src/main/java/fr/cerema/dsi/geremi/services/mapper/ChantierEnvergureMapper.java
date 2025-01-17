package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.entities.ChantierEnvergure;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.dto.ChantierEnvergureDTO;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public abstract class ChantierEnvergureMapper {

  @Autowired
  protected EtudeMapper etudeMapper;

  public Feature toFeature(ChantierEnvergure entity) {
    Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getTheGeom());
    Integer id = entity.getIdChantier();
    Map<String, Object> properties = new HashMap<>();
    properties.put("nom", entity.getNom());
    properties.put("description", entity.getDescription());
    properties.put("annee_debut", entity.getAnneeDebut());
    properties.put("annee_fin", entity.getAnneeFin());
    properties.put("beton_pref", entity.getBetonPref());
    properties.put("viab_autre", entity.getViabAutre());
    properties.put("ton_tot", entity.getTonTot());
    if(Objects.nonNull(entity.getChantierSource())){
      properties.put("id_source", entity.getChantierSource().getIdChantier());
      properties.put("libelle_pere", entity.getChantierSource().getNom());
    } else {
      properties.put("id_source", null);
      properties.put("libelle_pere", null);
    }
    properties.put("id_frere", entity.getIdFrere());
    properties.put("id_etude", entity.getEtude().getId());

    ChantierEnvergureMapper.log.debug("Converting Chantier to Feature: {}",
      entity.toString());
    return new Feature(id, geometry, properties);
  }

  public Feature toFeature(ChantierEnvergureDTO dto) {
    Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(dto.getTheGeom());
    Integer id = dto.getIdChantier();
    Map<String, Object> properties = new HashMap<>();
    properties.put("nom", dto.getNom());
    properties.put("description", dto.getDescription());
    properties.put("annee_debut", dto.getAnneeDebut());
    properties.put("annee_fin", dto.getAnneeFin());
    properties.put("beton_pref", dto.getBetonPref());
    properties.put("viab_autre", dto.getViabAutre());
    properties.put("ton_tot", dto.getTonTot());
    properties.put("id_source", dto.getIdSource());
    properties.put("id_frere", dto.getIdFrere());
    properties.put("id_etude", dto.getEtudeDTO().getId());
    properties.put("libelle_pere", dto.getLibellePere());
    ChantierEnvergureMapper.log.debug("Converting ChantierDTO to Feature: {}",
      dto.toString());
    return new Feature(id, geometry, properties);
  }

  public List<Feature> chantiersToFeature(List<ChantierEnvergure> entities) {
    return entities.stream()
      .map(this::toFeature)
      .collect(Collectors.toList());
  }

  public List<Feature> chantiersDTOToFeature(List<ChantierEnvergureDTO> dtos) {
    return dtos.stream()
      .map(this::toFeature)
      .collect(Collectors.toList());
  }

  @Mapping(target = "etudeDTO", expression = "java(this.etudeMapper.toDto(chantierEnvergure.getEtude()))")
  public abstract ChantierEnvergureDTO toDto(ChantierEnvergure chantierEnvergure);

  @AfterMapping
  public void toDtoAfterMapping(ChantierEnvergure entitySource, @MappingTarget ChantierEnvergureDTO dtoTarget) {
    if (entitySource.getChantierSource() != null) {
      dtoTarget.setLibellePere(entitySource.getChantierSource().getNom());
      dtoTarget.setIdSource(entitySource.getChantierSource().getIdChantier());
    }
  }
}

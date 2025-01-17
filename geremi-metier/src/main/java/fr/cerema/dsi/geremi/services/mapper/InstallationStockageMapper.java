package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Geometry;

import fr.cerema.dsi.geremi.entities.InstallationStockage;
import fr.cerema.dsi.geremi.services.dto.InstallationStockageDTO;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public abstract class InstallationStockageMapper {

    public Feature toFeature(InstallationStockage entity) {
        Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getTheGeom());
        Integer id = entity.getIdStockage();
        Map<String, Object> properties = new HashMap<>();
        properties.put("nom_etab", entity.getNomEtab());
        properties.put("code_etab", entity.getCodeEtab());
        properties.put("description", entity.getDescription());
        properties.put("annee_debut", entity.getAnneeDebut());
        properties.put("annee_fin", entity.getAnneeFin());
        properties.put("beton_pref", entity.getBetonPref());
        properties.put("viab_autre", entity.getViabAutre());
        properties.put("ton_tot", entity.getTonTot());
        if(Objects.nonNull(entity.getInstallationStockageSource())){
          properties.put("id_source", entity.getInstallationStockageSource().getIdStockage());
          properties.put("libelle_pere", entity.getInstallationStockageSource().getNomEtab());
        } else {
          properties.put("id_source", null);
          properties.put("libelle_pere", null);
        }
        properties.put("id_frere", entity.getIdFrere());
        properties.put("id_etude", entity.getEtude().getId());
        InstallationStockageMapper.log.debug("Converting InstallationStockage to Feature: {}", entity.toString());
        return new Feature(id, geometry, properties);
    }


    public List<Feature> installationsToFeature(List<InstallationStockage> entities) {
        return entities.stream()
            .map(this::toFeature)
            .collect(Collectors.toList());
    }

  //@Mapping(target = "etudeDTO", expression = "java(this.etudeService.getEtudeById(installationStockage.getEtude().getId()).orElse(null))")
  public abstract InstallationStockageDTO toDto(InstallationStockage installationStockage);

  @AfterMapping
  public void toDtoAfterMapping(InstallationStockage entitySource, @MappingTarget InstallationStockageDTO dtoTarget) {
    if (entitySource.getInstallationStockageSource() != null) {
      dtoTarget.setLibellePere(entitySource.getInstallationStockageSource().getNomEtab());
      dtoTarget.setIdSource(entitySource.getInstallationStockageSource().getIdStockage());
    }
  }
}

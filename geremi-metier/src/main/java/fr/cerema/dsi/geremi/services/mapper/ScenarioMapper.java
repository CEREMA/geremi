package fr.cerema.dsi.geremi.services.mapper;

import java.util.HashMap;
import java.util.List;

import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioChantierService;
import fr.cerema.dsi.geremi.services.ScenarioContrainteService;
import fr.cerema.dsi.geremi.services.ScenarioInstallationService;
import fr.cerema.dsi.geremi.services.ScenarioMateriauService;
import fr.cerema.dsi.geremi.services.dto.ResultatCalculDTO;
import fr.cerema.dsi.geremi.services.dto.ResultatZoneDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ScenarioMapper {

  @Autowired
  protected EtudeMapper etudeMapper;
  @Autowired
  protected ScenarioContrainteService scenarioContrainteService;
  @Autowired
  protected ScenarioInstallationService scenarioInstallationService;
  @Autowired
  protected ScenarioChantierService scenarioChantierService;
  @Autowired
  protected ScenarioMateriauService scenarioMateriauService;
  @Autowired
  protected RelScenarioDepartementService relScenarioDepartementService;
  @Autowired
  protected ResultatCalculService resultatCalculService;
  @Autowired
  protected RelResultatZoneService relResultatZoneService;
  @Autowired
  protected RelScenarioZoneService relScenarioZoneService;

  @Mapping(target = "ifRetenu", expression = "java(entity.getIfRetenu())")
  @Mapping(target = "etudeDTO", expression = "java(this.etudeMapper.toDto(entity.getEtude()))")
  @Mapping(target = "scenarioContraintes", expression = "java(this.scenarioContrainteService.getListeScenarioContraintes(entity.getId()).orElse(null))")
  @Mapping(target = "scenarioInstallations", expression = "java(this.scenarioInstallationService.getListeScenarioInstallations(entity.getId()).orElse(null))")
  @Mapping(target = "scenarioChantiers", expression = "java(this.scenarioChantierService.getListeScenarioChantiers(entity.getId()).orElse(null))")
  @Mapping(target = "scenarioMateriaux", expression = "java(this.scenarioMateriauService.getListeScenarioMateriaux(entity.getId()).orElse(null))")
  @Mapping(target = "relScenarioDepartement", expression = "java(this.relScenarioDepartementService.getListeRelScenarioDepartement(entity.getId()).orElse(null))")
  @Mapping(target = "zoneProductionDetails", expression = "java(this.relScenarioZoneService.findByIdScenario(entity.getId()).orElse(null))")
  @Mapping(target = "resultatsCalculs", ignore = true)
  public abstract ScenarioDTO toDto(Scenario entity);

  @Mapping(target = "etude", expression = "java(this.etudeMapper.toEntity(scenarioDTO.getEtudeDTO()))")
  public abstract Scenario toEntity(ScenarioDTO scenarioDTO);

  @AfterMapping
  public void toDtoAfterMapping(Scenario entitySource, @MappingTarget ScenarioDTO dtoTarget) {
    dtoTarget.setResultatsCalculs(new HashMap<>());

    List<ResultatCalcul> resultatCalculList = this.resultatCalculService.findByIdScenarioForCalcul(entitySource.getId());
    if (resultatCalculList != null) {

      for (ResultatCalcul rc : resultatCalculList) {
        ResultatCalculDTO resultatCalculDTO = toResultatCalculDto(rc);
        resultatCalculDTO.setIdEtude(dtoTarget.getEtudeDTO().getId());
        resultatCalculDTO.setResultatZones(new HashMap<>());

        List<RelResultatZone> resZones = this.relResultatZoneService.findByIdResultat(rc.getId());
        for (RelResultatZone res : resZones) {
          ResultatZoneDTO resDTO = toResultatZoneDto(res);
          resDTO.setAnnee(rc.getAnnee());
          resDTO.setIdEtude(dtoTarget.getEtudeDTO().getId());
          resDTO.setIdScenario(dtoTarget.getId());

          resultatCalculDTO.getResultatZones().put(resDTO.getIdZone(), resDTO);
        }

        dtoTarget.getResultatsCalculs().put(resultatCalculDTO.getAnnee(), resultatCalculDTO);
      }
    }
  }

  @Mapping(target = "productionTerritoirePrimaireTotal", source = "productionTerritoirePrimaire")
  @Mapping(target = "productionTerritoireSecondaireTotal", source = "productionTerritoireSecondaire")
  @Mapping(target = "productionTerritoirePrimaireBrute", source = "productionTerritoirePrimaireBrut")
  @Mapping(target = "idEtude", ignore = true)
  @Mapping(target = "resultatZones", ignore = true)
  public abstract ResultatCalculDTO toResultatCalculDto(ResultatCalcul entity);


  @Mapping(target = "productionZonePrimaireTotal", source = "productionZonePrimaire")
  @Mapping(target = "productionZoneSecondaireTotal", source = "productionZoneSecondaire")
  @Mapping(target = "productionZonePrimaireBrute", source = "productionZonePrimaireBrut")
  @Mapping(target = "pourcentageProductionZoneSecondaire", source = "pourcentProductionZoneSecondaire")
  @Mapping(target = "idEtude", ignore = true)
  @Mapping(target = "idScenario", ignore = true)
  @Mapping(target = "annee", ignore = true)
  public abstract ResultatZoneDTO toResultatZoneDto(RelResultatZone entity);

}

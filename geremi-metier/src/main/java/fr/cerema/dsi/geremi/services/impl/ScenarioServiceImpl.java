package fr.cerema.dsi.geremi.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.CalculsProductionAndProjectionService;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioChantierService;
import fr.cerema.dsi.geremi.services.ScenarioContrainteService;
import fr.cerema.dsi.geremi.services.ScenarioInstallationService;
import fr.cerema.dsi.geremi.services.ScenarioMateriauService;
import fr.cerema.dsi.geremi.services.ScenarioService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioServiceImpl extends GenericServiceImpl<Scenario, Long> implements ScenarioService {

  @Autowired
  private ScenarioRepository scenarioRepository;
  @Autowired
  private ScenarioMapper scenarioMapper;
  @Autowired
  private TracabiliteEtapeService tracabiliteEtapeService;
  @Autowired
  private ScenarioContrainteService scenarioContrainteService;
  @Autowired
  private ScenarioChantierService scenarioChantierService;
  @Autowired
  private ScenarioInstallationService scenarioInstallationService;
  @Autowired
  private ScenarioMateriauService scenarioMateriauService;
  @Autowired
  private RelResultatZoneService relResultatZoneService;
  @Autowired
  private ResultatCalculService resultatCalculService;
  @Autowired
  private RelScenarioDepartementService relScenarioDepartementService;
  @Autowired
  private RelScenarioZoneService relScenarioZoneService;
  @Autowired
  private CalculsProductionAndProjectionService calculsProductionAndProjectionService;

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.scenarioRepository.getListScenarioEtude(idEtude).forEach(s -> this.deleteById(s.getId()));
  }

  @Override
  @Transactional
  public Optional<ScenarioDTO> setScenarioRetenu(ScenarioDTO scenarioDTO) {
    // Enlève l'ancien scénario retenu
    this.scenarioRepository.updateScenarioRetenu(scenarioDTO.getEtudeDTO().getId());

    Scenario scenario = this.scenarioRepository.getOne(scenarioDTO.getId());
    scenario.setIfRetenu(scenarioDTO.isIfRetenu());
    return Optional.of(this.scenarioMapper.toDto(this.scenarioRepository.save(scenario)));
  }

  @Override
  public Scenario getOne(Long aLong) {
    return this.scenarioRepository.getOne(aLong);
  }

  @Override
  public Scenario findById(Long aLong) {
    return this.scenarioRepository.findById(aLong).orElse(null);
  }

  @Override
  public Scenario create(Scenario entity) {
    return this.scenarioRepository.save(entity);
  }

  @Override
  @Transactional
  public void deleteById(Long aLong) {
    this.tracabiliteEtapeService.deleteByIdScenario(aLong);
    this.scenarioContrainteService.deleteById(aLong);
    this.scenarioChantierService.deleteByIdScenario(aLong);
    this.scenarioInstallationService.deleteByIdScenario(aLong);
    this.scenarioMateriauService.deleteById(aLong);
    this.relScenarioDepartementService.deleteByIdScenario(aLong);
    this.relScenarioZoneService.deleteByIdScenario(aLong);

    List<ResultatCalcul> tmp = this.resultatCalculService.findByIdScenario(aLong);
    if(Objects.nonNull(tmp)){
      for (ResultatCalcul res : tmp) {
        for (RelResultatZone rel : this.relResultatZoneService.findByIdResultat(res.getId())) {
          this.relResultatZoneService.deleteById(rel.getId());
        }
        this.resultatCalculService.deleteById(res.getId());
      }
    }
    this.scenarioRepository.deleteById(aLong);
  }

  @Override
  public Scenario save(Scenario entity) {
    return this.scenarioRepository.save(entity);
  }

  @Override
  public Optional<List<ScenarioDTO>> getListeScenarioEtude(Long idEtude) {
    List<ScenarioDTO> scenarioDTOS = this.scenarioRepository.getListScenarioEtude(idEtude)
      .stream()
      .map(this.scenarioMapper::toDto)
      .peek(s -> s.setEtatEtapes(this.tracabiliteEtapeService.getEtatEtapeEtude(idEtude, s.getId())))
      .toList();

    if(scenarioDTOS.isEmpty()){
      return Optional.empty();
    }
    return Optional.of(scenarioDTOS);
  }

  @Override
  public Optional<ScenarioDTO> getScenarioById(Long idScenario) {
    Scenario scenario = this.findById(idScenario);
    if(Objects.nonNull(scenario)){
      return Optional.of(this.scenarioMapper.toDto(scenario));
    }
    return Optional.empty();
  }

  @Override
  public Optional<ScenarioDTO> createScenario(ScenarioDTO scenarioDTO) {
    scenarioDTO.setDateMaj(LocalDateTime.now());
      if(scenarioDTO.getId().equals(0L)){
        scenarioDTO.setId(null);
      }
    Scenario scenario = this.create(this.scenarioMapper.toEntity(scenarioDTO));
    if(Objects.nonNull(scenario)){
      this.tracabiliteEtapeService.addTracabiliteEtape(scenario.getEtude().getId(), scenario.getId(), Etape.CREATION_SCENARIO, EtatEtape.VALIDE);
      return Optional.of(this.scenarioMapper.toDto(scenario));
    }
    return Optional.empty();
  }

  @Override
  public Optional<ScenarioDTO> createScenarioFromImport(ScenarioDTO scenarioDTO) {
    scenarioDTO.setDateMaj(LocalDateTime.now());
    if(scenarioDTO.getId().equals(0L)){
      scenarioDTO.setId(null);
    }
    Scenario scenario = this.create(this.scenarioMapper.toEntity(scenarioDTO));
    if(Objects.nonNull(scenario)){
      return Optional.of(this.scenarioMapper.toDto(scenario));
    }
    return Optional.empty();
  }

  @Override
  public Optional<ScenarioDTO> updateScenario(ScenarioDTO scenarioDTO) {
    ScenarioDTO oldScenario = this.getScenarioById(scenarioDTO.getId()).orElse(null);

    scenarioDTO.setDateMaj(LocalDateTime.now());
    Scenario scenario = this.save(this.scenarioMapper.toEntity(scenarioDTO));

    if(Objects.nonNull(oldScenario) && !oldScenario.getDynamiqueDemographique().equals(scenario.getDynamiqueDemographique())){

      Map<Etape, EtatEtape> etatEtapeMap = this.tracabiliteEtapeService.getEtatEtapeEtude(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId());

      if(etatEtapeMap.get(Etape.HYPOTHESE_PROJECTION_SCENARIO).equals(EtatEtape.VALIDE)){
        this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.HYPOTHESE_PROJECTION_SCENARIO, EtatEtape.NON_RENSEIGNE);
        this.relScenarioZoneService.deleteByIdScenario(scenarioDTO.getId());
      }
      if(etatEtapeMap.get(Etape.HYPOTHESE_VENTILATION_SCENARIO).equals(EtatEtape.VALIDE)) {
        this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.HYPOTHESE_VENTILATION_SCENARIO, EtatEtape.NON_RENSEIGNE);

        List<ResultatCalcul> resultatCalculs = this.resultatCalculService.findByIdScenario(scenarioDTO.getId());
        for ( ResultatCalcul resCal : resultatCalculs) {
          this.relResultatZoneService.delete(this.relResultatZoneService.findByIdResultat(resCal.getId()));
        }
        resultatCalculService.delete(resultatCalculs);

        scenarioDTO.getEtatEtapes().put(Etape.HYPOTHESE_VENTILATION_SCENARIO,EtatEtape.NON_RENSEIGNE);
        scenarioDTO.getEtatEtapes().put(Etape.HYPOTHESE_PROJECTION_SCENARIO,EtatEtape.NON_RENSEIGNE);
      }

    }
    if(Objects.nonNull(scenario)){
      this.tracabiliteEtapeService.addTracabiliteEtape(scenario.getEtude().getId(), scenario.getId(), Etape.CREATION_SCENARIO, EtatEtape.VALIDE);
      return Optional.of(this.scenarioMapper.toDto(scenario));
    }

    return Optional.empty();
  }

  @Override
  public ScenarioDTO suiviScenarioEtude(Long idEtude) {
    return this.scenarioRepository.getIdScenarioRetenuEtude(idEtude).flatMap(this::getScenarioById).map(s -> {
      if (s.getEtudeDTO().isIfImporte()){
        return s;
      } else {
        return calculsProductionAndProjectionService.recalculSuiviScenarioProdReelle(s);
      }
    }).orElse(null);
  }
}

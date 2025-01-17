package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.ScenarioMateriau;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.ScenarioMateriauRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioMateriauService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioMateriauDTO;
import fr.cerema.dsi.geremi.services.mapper.ScenarioMateriauMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("scenarioMateriauService")
public class ScenarioMateriauServiceImpl implements ScenarioMateriauService {

  @Autowired
  private ScenarioMateriauRepository scenarioMateriauRepository;
  @Autowired
  private ScenarioMateriauMapper scenarioMateriauMapper;
  @Autowired
  private TracabiliteEtapeService tracabiliteEtapeService;
  @Autowired
  private RelResultatZoneService relResultatZoneService;
  @Autowired
  private ResultatCalculService resultatCalculService;
  @Autowired
  private RelScenarioDepartementService relScenarioDepartementService;
  @Autowired
  private RelScenarioZoneService relScenarioZoneService;

  @Override
  public Optional<List<ScenarioMateriauDTO>> getListeScenarioMateriaux(Long idScenario) {
    return Optional.of(this.scenarioMateriauRepository.getListScenarioMateriau(idScenario).stream().map(this.scenarioMateriauMapper::toDto).toList());
  }

  @Override
  public void deleteById(Long idScenario) {
    this.scenarioMateriauRepository.deleteByIdScenario(idScenario);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ScenarioDTO ajoutMateriauScenario(ScenarioDTO scenarioDTO) {
    // Supprimez toutes les materiaux existants pour le scénario spécifique.

    this.scenarioMateriauRepository.deleteByIdScenario(scenarioDTO.getId());

    // Enregistrez les nouveaux Materiaux.
    List<ScenarioMateriau> scenarioMateriaux = scenarioDTO.getScenarioMateriaux().stream()
      .map(this.scenarioMateriauMapper::toEntity).toList();

    scenarioMateriaux = this.scenarioMateriauRepository.saveAll(scenarioMateriaux);

    Map<Etape, EtatEtape> etatEtapeMap = this.tracabiliteEtapeService.getEtatEtapeEtude(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId());
    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.MATERIAUX_SCENARIO, EtatEtape.VALIDE);

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

    List<ScenarioMateriauDTO> scenarioMateriauDTOS = scenarioMateriaux.stream()
      .map(scenarioMateriauMapper::toDto).toList();
    scenarioDTO.setScenarioMateriaux(scenarioMateriauDTOS);
    return scenarioDTO;
  }
}

package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.entities.ScenarioContrainte;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.ScenarioContrainteRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioContrainteService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ScenarioContrainteDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.mapper.ScenarioContrainteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("scenarioContrainteService")
public class ScenarioContrainteServiceImpl implements ScenarioContrainteService {

  private final ScenarioContrainteRepository scenarioContrainteRepository;
  private final ScenarioContrainteMapper scenarioContrainteMapper;
  private final ScenarioRepository scenarioRepository;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final RelResultatZoneService relResultatZoneService;
  private final ResultatCalculService resultatCalculService;
  private final RelScenarioDepartementService relScenarioDepartementService;
  private final RelScenarioZoneService relScenarioZoneService;

  public ScenarioContrainteServiceImpl(ScenarioContrainteRepository scenarioContrainteRepository,
                                       ScenarioContrainteMapper scenarioContrainteMapper,
                                       ScenarioRepository scenarioRepository,
                                       TracabiliteEtapeService tracabiliteEtapeService,
                                       RelResultatZoneService relResultatZoneService,
                                       ResultatCalculService resultatCalculService,
                                       RelScenarioDepartementService relScenarioDepartementService,
                                       RelScenarioZoneService relScenarioZoneService) {
    this.scenarioContrainteRepository = scenarioContrainteRepository;
    this.scenarioContrainteMapper = scenarioContrainteMapper;
    this.scenarioRepository = scenarioRepository;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.relResultatZoneService = relResultatZoneService;
    this.resultatCalculService = resultatCalculService;
    this.relScenarioDepartementService = relScenarioDepartementService;
    this.relScenarioZoneService = relScenarioZoneService;
  }

  @Override
  public Optional<List<ScenarioContrainteDTO>> getListeScenarioContraintes(Long idScenario) {
    return Optional.of(this.scenarioContrainteRepository.getListScenarioContrainte(idScenario).stream().map(this.scenarioContrainteMapper::toDto).toList());
  }

  @Override
  public void deleteById(Long idScenario) {
    this.scenarioContrainteRepository.deleteByScenarioId(idScenario);
  }

  @Override
  @Transactional
  public ScenarioDTO ajoutContrainteScenario(ScenarioDTO scenarioDTO) {
    // Supprimez tous les contraintes existants pour le scénario spécifique.
    this.scenarioContrainteRepository.deleteByScenarioId(scenarioDTO.getId());

    // Enregistrer le taux hors contrainte
    this.updateTxRenouvellementHC(scenarioDTO);

    // Enregistrer les contraintes.
    List<ScenarioContrainte> scenarioContrainte = scenarioDTO.getScenarioContraintes().stream()
      .map(this.scenarioContrainteMapper::toEntity).collect(Collectors.toList());

    scenarioContrainte = this.scenarioContrainteRepository.saveAll(scenarioContrainte);

    Map<Etape, EtatEtape> etatEtapeMap = this.tracabiliteEtapeService.getEtatEtapeEtude(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId());
    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.CONTRAINTES_SCENARIO, EtatEtape.VALIDE);

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

    List<ScenarioContrainteDTO> scenarioContrainteDTOS = scenarioContrainte.stream()
      .map(this.scenarioContrainteMapper::toDto).collect(Collectors.toList());
    scenarioDTO.setScenarioContraintes(scenarioContrainteDTOS);
    return scenarioDTO;
  }

  private void updateTxRenouvellementHC(ScenarioDTO scenarioDTO) {
    Scenario scenario = this.scenarioRepository.findById(scenarioDTO.getId()).orElseThrow();
    scenario.setTxRenouvellementHc(scenarioDTO.getTxRenouvellementHc());
    this.scenarioRepository.save(scenario);
  }

}

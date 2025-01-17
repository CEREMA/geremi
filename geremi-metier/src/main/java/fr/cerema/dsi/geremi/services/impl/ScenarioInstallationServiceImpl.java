package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.ScenarioInstallation;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.ScenarioInstallationRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioInstallationService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioInstallationDTO;
import fr.cerema.dsi.geremi.services.mapper.ScenarioInstallationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("scenarioInstallationService")
public class ScenarioInstallationServiceImpl implements ScenarioInstallationService {

	private final ScenarioInstallationRepository scenarioInstallationRepository;
	private final ScenarioInstallationMapper scenarioInstallationMapper;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final RelResultatZoneService relResultatZoneService;
  private final ResultatCalculService resultatCalculService;
  private final RelScenarioDepartementService relScenarioDepartementService;
  private final RelScenarioZoneService relScenarioZoneService;

	public ScenarioInstallationServiceImpl(ScenarioInstallationRepository scenarioInstallationRepository,
                                         ScenarioInstallationMapper scenarioInstallationMapper,
                                         TracabiliteEtapeService tracabiliteEtapeService,
                                         RelResultatZoneService relResultatZoneService,
                                         ResultatCalculService resultatCalculService,
                                         RelScenarioDepartementService relScenarioDepartementService,
                                         RelScenarioZoneService relScenarioZoneService) {
		this.scenarioInstallationRepository = scenarioInstallationRepository;
		this.scenarioInstallationMapper = scenarioInstallationMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.relResultatZoneService = relResultatZoneService;
    this.resultatCalculService = resultatCalculService;
    this.relScenarioDepartementService = relScenarioDepartementService;
    this.relScenarioZoneService = relScenarioZoneService;
  }

  @Override
  public Optional<List<ScenarioInstallationDTO>> getListeScenarioInstallations(Long idScenario) {
    return Optional.of(this.scenarioInstallationRepository.getListScenarioInstallation(idScenario).stream().map(this.scenarioInstallationMapper::toDto).toList());
  }

  @Override
  public void deleteByIdScenario(Long idScenario) {
    this.scenarioInstallationRepository.deleteByScenarioId(idScenario);
  }

  @Override
	@Transactional
	public ScenarioDTO ajoutScenarioInstallation(ScenarioDTO scenarioDTO) {
		// Supprimez toutes les installations existantes pour le scénario spécifique.

		scenarioInstallationRepository.deleteByScenarioId(scenarioDTO.getId());

		// Enregistrez les nouvelles installations.
		List<ScenarioInstallation> scenarioInstallations = scenarioDTO.getScenarioInstallations().stream()
				.map(scenarioInstallationMapper::toEntity).collect(Collectors.toList());

		scenarioInstallations = scenarioInstallationRepository.saveAll(scenarioInstallations);

    Map<Etape, EtatEtape> etatEtapeMap = this.tracabiliteEtapeService.getEtatEtapeEtude(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId());
    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.INSTALLATIONS_SCENARIO, EtatEtape.VALIDE);

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

    List<ScenarioInstallationDTO> scenarioInstallationDTOS = scenarioInstallations.stream()
				.map(scenarioInstallationMapper::toDto).collect(Collectors.toList());
		scenarioDTO.setScenarioInstallations(scenarioInstallationDTOS);
		return scenarioDTO;
	}
}

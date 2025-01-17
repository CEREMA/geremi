package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.ScenarioChantier;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.ScenarioChantierRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import fr.cerema.dsi.geremi.services.ScenarioChantierService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ScenarioChantierDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.mapper.ScenarioChantierMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("scenarioChantierService")
public class ScenarioChantierServiceImpl implements ScenarioChantierService {

	private final ScenarioChantierRepository scenarioChantierRepository;

	private final ScenarioChantierMapper scenarioChantierMapper;

  private final TracabiliteEtapeService tracabiliteEtapeService;

  private final RelResultatZoneService relResultatZoneService;
  private final ResultatCalculService resultatCalculService;
  private final RelScenarioDepartementService relScenarioDepartementService;
  private final RelScenarioZoneService relScenarioZoneService;

	public ScenarioChantierServiceImpl(ScenarioChantierRepository scenarioChantierRepository,
                                     ScenarioChantierMapper scenarioChantierMapper,
                                     TracabiliteEtapeService tracabiliteEtapeService,
                                     RelResultatZoneService relResultatZoneService,
                                     ResultatCalculService resultatCalculService,
                                     RelScenarioDepartementService relScenarioDepartementService,
                                     RelScenarioZoneService relScenarioZoneService) {
		this.scenarioChantierRepository = scenarioChantierRepository;
		this.scenarioChantierMapper = scenarioChantierMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.relResultatZoneService = relResultatZoneService;
    this.resultatCalculService = resultatCalculService;
    this.relScenarioDepartementService = relScenarioDepartementService;
    this.relScenarioZoneService = relScenarioZoneService;
  }

  @Override
  public Optional<List<ScenarioChantierDTO>> getListeScenarioChantiers(Long idScenario) {
    return Optional.of(this.scenarioChantierRepository.getListScenarioChantier(idScenario).stream().map(this.scenarioChantierMapper::toDto).toList());
  }

  @Override
  public void deleteByIdScenario(Long idScenario) {
    this.scenarioChantierRepository.deleteByScenarioId(idScenario);
  }

  @Override
	@Transactional
	public ScenarioDTO ajoutScenarioChantier(ScenarioDTO scenarioDTO) {
		// Supprimez tous les chantiers existants pour le scénario spécifique.
		scenarioChantierRepository.deleteByScenarioId(scenarioDTO.getId());

		// Enregistrez les nouveaux chantiers.
		List<ScenarioChantier> scenarioChantiers = scenarioDTO.getScenarioChantiers().stream()
				.map(scenarioChantierMapper::toEntity).collect(Collectors.toList());

		scenarioChantiers = scenarioChantierRepository.saveAll(scenarioChantiers);

    Map<Etape, EtatEtape> etatEtapeMap = this.tracabiliteEtapeService.getEtatEtapeEtude(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId());
    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.CHANTIERS_SCENARIO, EtatEtape.VALIDE);

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

		List<ScenarioChantierDTO> scenarioChantierDTOS = scenarioChantiers.stream()
				.map(scenarioChantierMapper::toDto).collect(Collectors.toList());
		scenarioDTO.setScenarioChantiers(scenarioChantierDTOS);
		return scenarioDTO;
	}
}

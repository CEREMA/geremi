package fr.cerema.dsi.commons.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import fr.cerema.dsi.geremi.dto.ProductionZone;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.repositories.RelScenarioInstallationRepository;
import fr.cerema.dsi.geremi.repositories.RelScenarioMateriauRepository;
import fr.cerema.dsi.geremi.repositories.ResultatCalculRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CalculsCommunProductionAnneeAndProjectionService implements CalculsService {


  protected final RelScenarioMateriauRepository relScenarioMateriauRepository;
	protected final RelScenarioInstallationRepository relScenarioInstallationRepository;
	protected final RelResultatZoneService relResultatZoneService;
	protected final ResultatCalculRepository resultatCalculRepository;

	public CalculsCommunProductionAnneeAndProjectionService(
			RelScenarioMateriauRepository relScenarioMateriauRepository,
			RelScenarioInstallationRepository relScenarioInstallationRepository,
      RelResultatZoneService relResultatZoneService,
      ResultatCalculRepository resultatCalculRepository) {
		this.relScenarioMateriauRepository = relScenarioMateriauRepository;
		this.relScenarioInstallationRepository = relScenarioInstallationRepository;
		this.relResultatZoneService = relResultatZoneService;
		this.resultatCalculRepository = resultatCalculRepository;
	}
  protected RelResultatZone initNewRelResultatZone(Long idResultat, Long idZone){
    RelResultatZone relResultatZone = new RelResultatZone();
    relResultatZone.setIdResultat(idResultat);
    relResultatZone.setIdZone(idZone);
    relResultatZone.setDateMaj(LocalDateTime.now());
    return this.relResultatZoneService.save(relResultatZone);
  }

  protected ResultatCalcul initNewResultatCalcul(Long idScenario, Long idTerritoire, Integer annee){
    ResultatCalcul resultatCalcul = new ResultatCalcul();
    resultatCalcul.setIdScenario(idScenario);
    resultatCalcul.setIdTerritoire(idTerritoire);
    resultatCalcul.setAnnee(annee);
    resultatCalcul.setDateMaj(LocalDateTime.now());
    return resultatCalculRepository.save(resultatCalcul);
  }

	protected List<ProductionZone> productionZoneStock(Long idScenario, Integer anneeRef) {
		// Calcul du total des stocks des matériaux pour chaque zone, à l'année de référence :
    return relScenarioInstallationRepository.getTotalStocksInstallationIntersectZone(idScenario, anneeRef);
	}

	protected List<ProductionZone> productionZoneSecondaireAutre(Long idScenario) {
		List<String> origines = Arrays.asList(RECYCLE, ARTIFICIEL);
		return relScenarioMateriauRepository.getMaterialsForScenarioAndZoneByTypeAndOrigin(idScenario, SECONDAIRE, origines);
	}

	protected List<ProductionZone> productionZonePrimaireAutre(Long idScenario) {
		List<String> originesPrimaire = List.of(NATUREL);
    return relScenarioMateriauRepository.getMaterialsForScenarioAndZoneByTypeAndOrigin(idScenario, PRIMAIRE, originesPrimaire);
	}





}

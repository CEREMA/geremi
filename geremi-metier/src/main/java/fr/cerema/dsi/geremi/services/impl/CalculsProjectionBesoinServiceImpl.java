package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cerema.dsi.geremi.dto.BesoinTotalChantierAnneeZoneDTO;
import fr.cerema.dsi.geremi.entities.Population;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.repositories.PopulationRepository;
import fr.cerema.dsi.geremi.repositories.RelResultatZoneRepository;
import fr.cerema.dsi.geremi.repositories.ResultatCalculRepository;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.CalculsProjectionBesoinService;
import fr.cerema.dsi.geremi.services.dto.ResultatCalculDTO;
import fr.cerema.dsi.geremi.services.dto.ResultatZoneDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("calculsProjectionBesoinService")
public class CalculsProjectionBesoinServiceImpl implements CalculsProjectionBesoinService {

	private final ZoneRepository zoneRepository;

	private final ResultatCalculRepository resultatCalculRepository;
	private final RelResultatZoneRepository relResultatZoneRepository;
  private final PopulationRepository populationRepository;


	private static final String BASSE = "Basse";
	private static final String CENTRALE = "Centrale";
	private static final String HAUTE = "Haute";

	public CalculsProjectionBesoinServiceImpl(
			ZoneRepository zoneRepository,
			ResultatCalculRepository resultatCalculRepository,
      RelResultatZoneRepository relResultatZoneRepository,
      PopulationRepository populationRepository) {
		this.zoneRepository = zoneRepository;
		this.resultatCalculRepository = resultatCalculRepository;
		this.relResultatZoneRepository = relResultatZoneRepository;
		this.populationRepository = populationRepository;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<Integer, ResultatCalculDTO> calculerProjectionBesoin(ScenarioDTO scenarioDTO, Long idTerritoire) {

		// Récupération de l'année de référence et de l'année de fin
		Integer anneeRef = scenarioDTO.getEtudeDTO().getAnneeRef();
		Integer anneeFin = scenarioDTO.getEtudeDTO().getAnneeFin();

    List<Long> idZonesEtude = this.zoneRepository.listZoneIdByEtudeId(scenarioDTO.getEtudeDTO().getId());

    ResultatCalcul resultatCalculAnneeRef = resultatCalculRepository.findByIdScenarioAndAnneeAndIfProjection(scenarioDTO.getId(),anneeRef,false);

    List<BesoinTotalChantierAnneeZoneDTO> besoinTotalChantiersAnneeZone = resultatCalculRepository.besoinTotalChantierAnneeZone(scenarioDTO.getId());

    Map<Integer, ResultatCalculDTO> resultatCalculDTOMap = new HashMap<>();

			// SMART-450
    for (int annee = anneeRef; annee <= anneeFin; annee++) {
      int finalAnnee = annee;

      BigDecimal besoinTerritoireTotalChantier = BigDecimal.ZERO;
      BigDecimal besoinTerritoirePrimaireTotal = BigDecimal.ZERO;
      BigDecimal besoinTerritoireSecondaireTotal = BigDecimal.ZERO;
      BigDecimal besoinTerritoireTotal = BigDecimal.ZERO;


      ResultatCalculDTO resCal = new ResultatCalculDTO(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId(),annee);
      resCal.setResultatZones(new HashMap<>());

      for (Long zoneId : idZonesEtude) {
        // Récupération des populations
        Population populationZoneAnneeEntity = populationRepository.getPopulationByZoneAndYear(zoneId,annee);
        BigDecimal populationZoneAnnee = calculatePopulationValue(populationZoneAnneeEntity, scenarioDTO);

        // Récupération des consommations de référence
        RelResultatZone consommationRef = relResultatZoneRepository.findByIdResultatAndZone(resultatCalculAnneeRef.getId(), zoneId);
        BigDecimal consommationPrimaireRef = BigDecimal.valueOf(consommationRef.getProductionZonePrimaire());
        BigDecimal consommationSecondaireRef = BigDecimal.valueOf(consommationRef.getProductionZoneSecondaire());

        BigDecimal besoinTotalChantierZoneAnnee = besoinTotalChantiersAnneeZone.stream().filter(b -> finalAnnee == b.getAnnee() && zoneId.equals(b.getIdZone())).findFirst().map(BesoinTotalChantierAnneeZoneDTO::getBesoinTotalChantier).orElse(BigDecimal.ZERO).setScale(3, RoundingMode.HALF_UP);

        Population populationZoneAnneeReferenceEntity = populationRepository.getPopulationByZoneAndYear(zoneId,anneeRef);
        BigDecimal populationZoneAnneeReference = calculatePopulationValue(populationZoneAnneeReferenceEntity, scenarioDTO);

        // Calcul des besoins
        BigDecimal besoinZonePrimaire  = consommationPrimaireRef.multiply(populationZoneAnnee).divide(populationZoneAnneeReference, 3, RoundingMode.HALF_UP);
        BigDecimal besoinZoneSecondaire  = consommationSecondaireRef.multiply(populationZoneAnnee).divide(populationZoneAnneeReference, 3, RoundingMode.HALF_UP);
        BigDecimal besoinZoneTotal = besoinTotalChantierZoneAnnee.add(besoinZonePrimaire ).add(besoinZoneSecondaire );


        ResultatZoneDTO resZone = new ResultatZoneDTO(scenarioDTO.getEtudeDTO().getId(),scenarioDTO.getId(),annee, zoneId);
        resZone.setBesoinZoneTotalChantier(besoinTotalChantierZoneAnnee.doubleValue());
        resZone.setBesoinZonePrimaire(besoinZonePrimaire.doubleValue());
        resZone.setBesoinZoneSecondaire(besoinZoneSecondaire.doubleValue());
        resZone.setBesoinZoneTotal(besoinZoneTotal.doubleValue());

        //somme pour le territoire
        // SMART-470 Calcul des projections du territoire (productions et besoins)
        besoinTerritoireTotalChantier = besoinTerritoireTotalChantier.add(besoinTotalChantierZoneAnnee);
        besoinTerritoirePrimaireTotal = besoinTerritoirePrimaireTotal.add(besoinZonePrimaire);
        besoinTerritoireSecondaireTotal = besoinTerritoireSecondaireTotal.add(besoinZoneSecondaire);
        besoinTerritoireTotal = besoinTerritoireTotal.add(besoinZoneTotal);

        resCal.getResultatZones().put(zoneId, resZone);
			}

      resultatCalculDTOMap.put(annee, resCal);

		}

		log.debug("Le calcul de projection est terminé.");
		return resultatCalculDTOMap;
	}

	private BigDecimal calculatePopulationValue(Population population, ScenarioDTO scenarioDTO) {
		if (population == null) {
			return BigDecimal.ZERO;
		}
		return switch (scenarioDTO.getDynamiqueDemographique()) {
		case BASSE -> BigDecimal.valueOf(population.getPopulationBasse());
		case CENTRALE -> BigDecimal.valueOf(population.getPopulationCentrale());
		case HAUTE -> BigDecimal.valueOf(population.getPopulationHaute());
		default -> throw new IllegalArgumentException(
				"Mauvaise valeur pour dynamique_demographique: " + scenarioDTO.getDynamiqueDemographique());
		};
	}



}

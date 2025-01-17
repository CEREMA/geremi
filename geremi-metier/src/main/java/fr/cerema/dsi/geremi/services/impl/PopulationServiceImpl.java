package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Population;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.PopulationRepository;
import fr.cerema.dsi.geremi.services.PopulationService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;
import fr.cerema.dsi.geremi.services.mapper.PopulationMapper;

@Service("populationService")
public class PopulationServiceImpl extends GenericServiceImpl<Population, Integer> implements PopulationService {

  private PopulationRepository populationRepository;

  private PopulationMapper populationMapper;

  private TracabiliteEtapeService tracabiliteEtapeService;

  private final SecurityService securityService;

  private static final String BASSE = "Basse";

  private static final String CENTRALE = "Centrale";

  private static final String HAUTE = "Haute";


  public PopulationServiceImpl(PopulationRepository populationRepository, PopulationMapper populationMapper, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService) {
    this.populationRepository = populationRepository;
    this.populationMapper = populationMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.securityService = securityService;
  }

  @Override
  public List<PopulationDTO> addPopulations(Long idEtude, List<PopulationDTO> populationDTOs) {
    securityService.checkModificationEtude(idEtude, null, Etape.POPULATION);
    this.deleteByIdEtude(idEtude);
    List<PopulationDTO> result = new ArrayList<>();
    for (PopulationDTO populationDTO : populationDTOs) {
      Population population = this.populationMapper.toEntity(populationDTO);
      this.populationRepository.save(population);
      result.add(this.populationMapper.toDto(population));
    }
    tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.POPULATION, EtatEtape.VALIDE);
    return result;
  }

  @Override
  public List<PopulationDTO> findByEtude(Etude etude) {
    securityService.checkConsultationEtude(Optional.of(etude));
    return populationRepository.findByEtude(etude).stream().map(populationMapper::toDto).toList();
  }

  @Override
  public BigDecimal getPopulationByZoneYearAndDynamics(Long zoneId, int anneeRef, String dynamiqueDemographique) {
	    // Récupération de la Population selon le type.
	    Population population = this.populationRepository.getPopulationByZoneAndYear(zoneId, anneeRef);
      securityService.checkConsultationEtude(Optional.of(population.getEtude()));
	    Integer populationZoneAnneeReference = switch (dynamiqueDemographique) {
        case BASSE -> population.getPopulationBasse();
        case CENTRALE -> population.getPopulationCentrale();
        case HAUTE -> population.getPopulationHaute();
        default -> throw new IllegalArgumentException("Mauvaise valeur dynamique_demographique: " + dynamiqueDemographique);
      };
    return new BigDecimal(populationZoneAnneeReference);
	  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.populationRepository.deleteByIdEtude(idEtude);
  }
}

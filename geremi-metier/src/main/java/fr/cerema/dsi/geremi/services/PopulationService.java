package fr.cerema.dsi.geremi.services;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Population;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;

public interface PopulationService extends GenericService<Population, Integer> {

  List<PopulationDTO> findByEtude(Etude etude);

  List<PopulationDTO> addPopulations(Long idEtude, List<PopulationDTO> populationDTOs);

  public BigDecimal getPopulationByZoneYearAndDynamics(Long zoneId, int anneeRef, String dynamiqueDemographique);

  void deleteByIdEtude(Long idEtude);
}

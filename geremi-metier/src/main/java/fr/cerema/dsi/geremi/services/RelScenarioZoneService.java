package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.RelScenarioZone;
import fr.cerema.dsi.geremi.services.dto.ZoneProductionDetailsDTO;

public interface RelScenarioZoneService extends GenericService<RelScenarioZone,Long> {

  void deleteByIdScenario(Long idScenario);

  Optional<List<ZoneProductionDetailsDTO>> findByIdScenario(Long idScenario);

}

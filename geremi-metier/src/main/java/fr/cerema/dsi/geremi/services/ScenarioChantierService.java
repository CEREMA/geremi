package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.geremi.services.dto.ScenarioChantierDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

import java.util.List;
import java.util.Optional;

public interface ScenarioChantierService {

  Optional<List<ScenarioChantierDTO>> getListeScenarioChantiers(Long idScenario);
  void deleteByIdScenario(Long idScenario);
	ScenarioDTO ajoutScenarioChantier(ScenarioDTO scenarioDTO);
}

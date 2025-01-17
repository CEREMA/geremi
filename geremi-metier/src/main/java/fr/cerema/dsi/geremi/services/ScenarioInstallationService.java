package fr.cerema.dsi.geremi.services;


import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioInstallationDTO;

import java.util.List;
import java.util.Optional;

public interface ScenarioInstallationService {

  Optional<List<ScenarioInstallationDTO>> getListeScenarioInstallations(Long idScenario);
  void deleteByIdScenario(Long idScenario);
	ScenarioDTO ajoutScenarioInstallation(ScenarioDTO scenarioDTO);
}

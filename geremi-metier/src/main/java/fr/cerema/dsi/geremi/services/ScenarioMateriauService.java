package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioMateriauDTO;

import java.util.List;
import java.util.Optional;

public interface ScenarioMateriauService {
  Optional<List<ScenarioMateriauDTO>> getListeScenarioMateriaux(Long idScenario);
  void deleteById(Long idScenario);
  ScenarioDTO ajoutMateriauScenario(ScenarioDTO scenarioDTO);
}

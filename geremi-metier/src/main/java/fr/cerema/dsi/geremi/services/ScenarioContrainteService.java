package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.geremi.services.dto.ScenarioContrainteDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

import java.util.List;
import java.util.Optional;

public interface ScenarioContrainteService {
  Optional<List<ScenarioContrainteDTO>> getListeScenarioContraintes(Long idScenario);
  void deleteById(Long idScenario);
  ScenarioDTO ajoutContrainteScenario(ScenarioDTO scenarioDTO);
}

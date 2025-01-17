package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

public interface ScenarioService extends GenericService<Scenario, Long> {

  Optional<List<ScenarioDTO>> getListeScenarioEtude(Long idEtude);

	Optional<ScenarioDTO> getScenarioById(Long idScenario);

	Optional<ScenarioDTO> createScenario(ScenarioDTO scenarioDTO);

  Optional<ScenarioDTO> createScenarioFromImport(ScenarioDTO scenarioDTO);

  Optional<ScenarioDTO> updateScenario(ScenarioDTO scenarioDTO);

  void deleteByIdEtude(Long idEtude);

  ScenarioDTO suiviScenarioEtude(Long idEtude);

  Optional<ScenarioDTO> setScenarioRetenu(ScenarioDTO scenarioDTO);
}

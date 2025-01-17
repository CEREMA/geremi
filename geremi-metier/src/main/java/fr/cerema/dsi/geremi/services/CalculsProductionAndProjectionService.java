package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.commons.services.CalculsService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

public interface CalculsProductionAndProjectionService  extends CalculsService {

   ScenarioDTO executeCalculProductionAnneeRef(Long idEtude, Long idScenario, int anneeRef, ScenarioDTO scenarioDTO);
   ScenarioDTO projectionScenario(ScenarioDTO scenarioDTO);

  ScenarioDTO recalculSuiviScenarioProdReelle(ScenarioDTO scenarioDTO);

}

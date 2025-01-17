package fr.cerema.dsi.geremi.services;

import java.util.Map;

import fr.cerema.dsi.geremi.services.dto.ResultatCalculDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

public interface CalculsProjectionBesoinService {

  Map<Integer, ResultatCalculDTO> calculerProjectionBesoin(ScenarioDTO scenarioDTO, Long idTerritoire);
}

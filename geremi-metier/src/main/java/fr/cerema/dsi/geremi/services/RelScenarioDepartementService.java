package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.RelScenarioDepartement;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;

import java.util.List;
import java.util.Optional;

public interface RelScenarioDepartementService extends GenericService<RelScenarioDepartement,Long> {
  Optional<List<RelScenarioDepartementDTO>> getListeRelScenarioDepartement(Long idScenario);
  void deleteByIdScenario(Long idScenario);
  List<RelScenarioDepartementDTO> saveAll(Long idScenario, List<RelScenarioDepartementDTO> relScenarioDepartementDTOs);
}

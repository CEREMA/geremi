package fr.cerema.dsi.geremi.services;

import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;

public interface ResultatCalculService extends GenericService<ResultatCalcul,Long> {
  List<ResultatCalcul> findByIdScenario(Long idScenario);
  List<ResultatCalcul> findByIdScenarioForCalcul(Long idScenario);

  void delete(ResultatCalcul entity);

  void delete(List<ResultatCalcul> entities);
}

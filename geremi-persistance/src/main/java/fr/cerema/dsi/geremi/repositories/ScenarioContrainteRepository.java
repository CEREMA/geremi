package fr.cerema.dsi.geremi.repositories;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ScenarioContrainte;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScenarioContrainteRepository extends GenericRepository<ScenarioContrainte, Long> {
  @Query(value = "SELECT * FROM {h-schema}rel_scenario_contrainte WHERE id_scenario=:idScenario", nativeQuery = true)
  List<ScenarioContrainte> getListScenarioContrainte(@Param("idScenario") Long idScenario);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}rel_scenario_contrainte WHERE id_scenario = :idScenario", nativeQuery = true)
  void deleteByScenarioId(@Param("idScenario") Long idScenario);
}

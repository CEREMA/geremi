package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ScenarioInstallation;

public interface ScenarioInstallationRepository extends GenericRepository<ScenarioInstallation, Long> {

  @Query(value = "SELECT * FROM {h-schema}rel_scenario_installation WHERE id_scenario=:idScenario", nativeQuery = true)
  List<ScenarioInstallation> getListScenarioInstallation(@Param("idScenario") Long idScenario);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}rel_scenario_installation WHERE id_scenario = :idScenario", nativeQuery = true)
  void deleteByScenarioId(@Param("idScenario") Long idScenario);
}

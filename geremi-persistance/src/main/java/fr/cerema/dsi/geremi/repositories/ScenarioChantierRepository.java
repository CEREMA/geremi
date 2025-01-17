package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ScenarioChantier;

public interface ScenarioChantierRepository extends GenericRepository<ScenarioChantier, Long> {
	
  @Query(value = "SELECT * FROM {h-schema}rel_scenario_chantier WHERE id_scenario=:idScenario", nativeQuery = true)
  List<ScenarioChantier> getListScenarioChantier(@Param("idScenario") Long idScenario);
  
  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}rel_scenario_chantier WHERE id_scenario = :idScenario", nativeQuery = true)
  void deleteByScenarioId(@Param("idScenario") Long idScenario);
}

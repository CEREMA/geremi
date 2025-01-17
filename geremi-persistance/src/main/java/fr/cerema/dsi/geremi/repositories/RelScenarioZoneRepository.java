package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RelScenarioZone;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelScenarioZoneRepository extends GenericRepository<RelScenarioZone, Long> {

  RelScenarioZone findByIdScenarioAndIdZone(Long idScenario, Long idZone);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}rel_scenario_zone WHERE id_scenario=:idScenario", nativeQuery = true)
  void deleteByIdScenario(@Param("idScenario") Long idScenario);


  List<RelScenarioZone> findByIdScenario(@Param("idScenario") Long idScenario);

}

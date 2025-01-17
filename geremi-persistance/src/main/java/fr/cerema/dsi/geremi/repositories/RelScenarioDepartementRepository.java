package fr.cerema.dsi.geremi.repositories;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RelScenarioDepartement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelScenarioDepartementRepository extends GenericRepository<RelScenarioDepartement,Long> {

  @Query(value = "SELECT * FROM {h-schema}rel_scenario_departement WHERE id_scenario=:idScenario", nativeQuery = true)
  List<RelScenarioDepartement> getListeRelScenarioDepartement(@Param("idScenario") Long idScenario);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}rel_scenario_departement WHERE id_scenario=:idScenario", nativeQuery = true)
  void deleteByIdScenario(@Param("idScenario") Long idScenario);
}

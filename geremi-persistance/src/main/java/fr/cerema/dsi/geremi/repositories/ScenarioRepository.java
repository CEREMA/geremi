package fr.cerema.dsi.geremi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Scenario;

public interface ScenarioRepository extends GenericRepository<Scenario, Long> {

	@Query(value = "SELECT s.* FROM {h-schema}scenario s WHERE s.id_scenario = :idScenario AND s.id_etude = :idEtude", nativeQuery = true)
	Scenario getScenario(@Param("idScenario") Long idScenario, @Param("idEtude") Long idEtude);

	@Query(value = "SELECT * FROM {h-schema}scenario WHERE id_etude=:idEtude", nativeQuery = true)
	List<Scenario> getListScenarioEtude(@Param("idEtude") Long idEtude);

  @Modifying
  @Query(value = "UPDATE {h-schema}scenario SET if_retenu = false WHERE id_etude=:idEtude", nativeQuery = true)
  void updateScenarioRetenu(@Param("idEtude") Long idEtude);

  @Query(value = "SELECT id_scenario FROM {h-schema}scenario WHERE id_etude=:idEtude and if_retenu = true", nativeQuery = true)
  Optional<Long> getIdScenarioRetenuEtude(@Param("idEtude") Long idEtude);
}

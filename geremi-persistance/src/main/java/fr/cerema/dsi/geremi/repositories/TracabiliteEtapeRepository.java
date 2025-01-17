package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.TracabiliteEtape;

public interface TracabiliteEtapeRepository extends GenericRepository<TracabiliteEtape, Long> {

  List<TracabiliteEtape> findTracabiliteEtapeByEtude_IdAndAndScenario_Id(@NonNull Long idEtude, Long idScenario);

  @Modifying(clearAutomatically = true)
  @Query(value="DELETE FROM {h-schema}tracabilite WHERE id_scenario=:idScenario", nativeQuery = true)
  void deleteByIdScenario(@Param("idScenario") Long idScenario);
}


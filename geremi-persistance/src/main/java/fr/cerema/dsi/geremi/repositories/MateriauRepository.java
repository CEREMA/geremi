package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Materiau;

public interface MateriauRepository extends GenericRepository<Materiau, Integer> {

  @Query(value = "SELECT * FROM materiau WHERE id_etude=:id_etude",nativeQuery = true)
  List<Materiau> getMateriauxEtude(@Param("id_etude") Long idEtude);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}materiau WHERE id_etude = :idEtude", nativeQuery = true)
  void deleteByIdEtude(@Param("idEtude") Long idEtude);
}

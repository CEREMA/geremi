package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;

public interface RelEtudeUserProcurationRepository extends GenericRepository<RelEtudeUserProcuration, Long> {

  @Query(value = "SELECT * FROM rel_etude_user_procuration WHERE id_etude=:id_etude", nativeQuery = true)
  List<RelEtudeUserProcuration> findAllByIdEtude(@Param("id_etude") double id_etude);
}


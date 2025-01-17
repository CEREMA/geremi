package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelResultatZoneRepository extends GenericRepository<RelResultatZone,Long> {
  @Query(value = "SELECT * FROM rel_resultat_zone WHERE id_resultat=:idResultat", nativeQuery = true)
  List<RelResultatZone> findByIdResultat(@Param("idResultat") Long idResultat);

  @Query(value = "SELECT * FROM rel_resultat_zone WHERE id_resultat=:idResultat and id_zone=:idZone", nativeQuery = true)
  RelResultatZone  findByIdResultatAndZone(@Param("idResultat") Long idResultat, @Param("idZone") Long idZone);


}

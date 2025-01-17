package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Population;

public interface PopulationRepository extends GenericRepository<Population, Integer> {

  List<Population> findByEtude(Etude etude);

	@Query(value = "SELECT p.* FROM geremi.population p WHERE id_zone = :zoneId  AND annee = :anneeRef", nativeQuery = true)
	Population getPopulationByZoneAndYear(@Param("zoneId") Long zoneId, @Param("anneeRef") int anneeRef);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM geremi.population WHERE id_etude = :idEtude", nativeQuery = true)
  void deleteByIdEtude(@Param("idEtude") Long idEtude);
}


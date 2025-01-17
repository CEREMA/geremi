package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Declaration;
import org.springframework.data.jpa.repository.Query;

public interface DeclarationRepository extends GenericRepository<Declaration,String> {

  /**
   * Requête pour récupérer les années disponibles dans la table Declaration.
   *
   * @return la liste des années disponibles
   */
  @Query(value = "SELECT DISTINCT annee FROM {h-schema}declaration order by annee ASC", nativeQuery = true)
  List<String> findDistinctAnnees();
}

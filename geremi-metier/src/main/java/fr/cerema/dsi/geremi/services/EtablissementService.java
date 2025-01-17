package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.wololo.geojson.FeatureCollection;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Etablissement;

public interface EtablissementService extends GenericService<Etablissement,String> {


  /**
   * Récupère toutes les etablissement pour une année
   *
   * @param annee
   * @return la liste des etablissement sur l'année
   */
  FeatureCollection findEtablissementsAnnee(String annee);

  /**
   * Récupère toutes les etablissement pour une année ayant l'origine materiaux spécifiée
   *
   * @param annee
   * @param origineMat
   * @return la liste des etablissements sur l'année pour l'origine demandée
   */
  FeatureCollection findEtablissementsAnneeEtOrigineMat(String annee, String origineMat);

  /**
   * Récupère toutes les etablissement en raport avec le territoire d'une étude etude
   * - soit inclus géographiquement dans le territoire
   * - soit exportant vers un département inclus (tout ou partie) dans le territoire
   * - soit exportant france entière
   *
   * @param annee
   * @param idEtude
   * @param territoireSeul inclure seulement les établissement dans le territoire
   * @return la liste des etablissement liés à l'étude
   */
  FeatureCollection findEtablissementsAnneeEtude(String annee, Long idEtude, Boolean territoireSeul);

    /**
     * Récupère la liste des années distinctes présentes dans la table Etablissement
     *
     * @return la liste des années distinctes
     */
    List<String> findDistinctAnnees();
}

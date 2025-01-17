package fr.cerema.dsi.geremi.services;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Commune;
import fr.cerema.dsi.geremi.services.dto.CommuneDTO;
import org.wololo.geojson.Feature;

/**
 * Interface de service pour la gestion des communes.
 * Cette interface étend l'interface générique de service
 * pour les entités de type `Commune`.
 */
public interface CommuneService extends GenericService<Commune, String> {

    /**
     * Recherche les communes se trouvant dans une boîte définie par les coordonnées.
     *
     * @param bbox0 coordonnée x minimale de la boîte
     * @param bbox1 coordonnée y minimale de la boîte
     * @param bbox2 coordonnée x maximale de la boîte
     * @param bbox3 coordonnée y maximale de la boîte
     * @param precision précision de la recherche (en degrés décimaux)
     * @return la liste des communes trouvées dans la boîte
     */
    List<Feature> findInBox(double bbox0, double bbox1, double bbox2, double bbox3, BigDecimal precision);

  List<Feature> findInListeTerritoire(String territoire, List<Long> liste_id, BigDecimal precision);
}

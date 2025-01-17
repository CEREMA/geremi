package fr.cerema.dsi.geremi.services;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Departement;
import fr.cerema.dsi.geremi.services.dto.DepartementDTO;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;
import org.wololo.geojson.Feature;

/**
 * Interface de service pour la gestion des départements.
 * Cette interface étend l'interface générique de service
 * pour les entités de type `Departement`.
 */

public interface DepartementService extends GenericService<Departement,Long>  {

	/**
	 * Recherche les départements se trouvant dans
	 * une boîte définie par les coordonnées.
	 *
	 * @param bbox0 coordonnée x minimale de la boîte
	 * @param bbox1 coordonnée y minimale de la boîte
	 * @param bbox2 coordonnée x maximale de la boîte
	 * @param bbox3 coordonnée y maximale de la boîte
	 * @return la liste des départements trouvés dans la boîte
	 */
	  List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision);

  List<Feature> findInListeRegion(List<Long> liste_id, BigDecimal precision);

  List<RelScenarioDepartementDTO> findDepartementPartielInEtude(Long idEtude);
}

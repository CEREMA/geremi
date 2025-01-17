package fr.cerema.dsi.geremi.services;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Region;
import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import org.wololo.geojson.Feature;

/**
 * Interface de service pour la gestion des régions.
 * Cette interface étend l'interface générique de service
 * pour les entités de type `Region`.
 */
public interface RegionService extends GenericService<Region,Long>  {

	/**
	 * Recherche les régions se trouvant dans une
	 * boîte définie par les coordonnées.
	 *
	 * @param bbox0 coordonnée x minimale de la boîte
	 * @param bbox1 coordonnée y minimale de la boîte
	 * @param bbox2 coordonnée x maximale de la boîte
	 * @param bbox3 coordonnée y maximale de la boîte
	 * @return la liste des régions trouvés dans la boîte
	 */
	List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision);

  RegionDTO findRegionById(Long id, double precision);
}

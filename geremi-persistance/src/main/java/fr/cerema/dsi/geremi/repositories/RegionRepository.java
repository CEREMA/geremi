package fr.cerema.dsi.geremi.repositories;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Region;

/**
 * Interface de répository pour les opérations CRUD sur les objets Region.
 * Cette interface hérite de l'interface générique {@link fr.cerema.dsi.commons.repositories.GenericRepository}
 * pour hériter de ses méthodes de base.
 */
public interface RegionRepository extends GenericRepository<Region,Long> {


	/**
	 * Méthode pour trouver les objets Departement qui se trouvent dans une boîte englobante donnée.
	 *
	 * @param bbox0 latitude minimale de la boîte englobante
	 * @param bbox1 longitude minimale de la boîte englobante
	 * @param bbox2 latitude maximale de la boîte englobante
	 * @param bbox3 longitude maximale de la boîte englobante
	 *
	 * @return une liste d'objets Region qui se trouvent dans la boîte englobante donnée.
	 */
  @Query(value= """
                SELECT r.id as id, r.nom_region as nom, r.insee_region as code, public.ST_REDUCEPRECISION(g.the_geom,:precision) as geomInt
                 FROM ref_region r
                 INNER JOIN ref_region_geometry g ON r.id=g.id
                 WHERE public.ST_INTERSECTS(public.ST_MakeEnvelope(:bbox0,:bbox1,:bbox2,:bbox3,4326), g.the_geom)
                """,nativeQuery = true)
  List<SelectionZonage> findInBox(@Param("bbox0") double  bbox0, @Param("bbox1") double  bbox1, @Param("bbox2") double  bbox2, @Param("bbox3") double  bbox3, @Param("precision") BigDecimal precision);

}

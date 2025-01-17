package fr.cerema.dsi.geremi.repositories;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Departement;

/**
 * Interface de répository pour les opérations CRUD sur les objets Departement.
 * Cette interface hérite de l'interface générique {@link fr.cerema.dsi.commons.repositories.GenericRepository}
 * pour hériter de ses méthodes de base.
 */
public interface DepartementRepository extends GenericRepository<Departement,Long> {


	/**
	 * Méthode pour trouver les objets Departement qui se trouvent dans une boîte englobante donnée.
	 *
	 * @param bbox0 latitude minimale de la boîte englobante
	 * @param bbox1 longitude minimale de la boîte englobante
	 * @param bbox2 latitude maximale de la boîte englobante
	 * @param bbox3 longitude maximale de la boîte englobante
	 *
	 * @return une liste d'objets Departement qui se trouvent dans la boîte englobante donnée.
	 */
	@Query(value="""
              SELECT d.id as id, d.nom_departement as nom, d.insee_departement as code, public.ST_REDUCEPRECISION(g.the_geom,:precision) as geomInt
              FROM ref_departement d
              INNER JOIN ref_departement_geometry g ON d.id=g.id
              WHERE public.ST_INTERSECTS(public.ST_MakeEnvelope(:bbox0, :bbox1, :bbox2, :bbox3, 4326), g.the_geom)
              """,nativeQuery = true)
	List<SelectionZonage> findInBox(@Param("bbox0") double  bbox0, @Param("bbox1") double  bbox1, @Param("bbox2") double  bbox2, @Param("bbox3") double  bbox3, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT d.id as id,
        d.nom_departement as nom,
        d.insee_departement as code,
        public.ST_REDUCEPRECISION(dg.the_geom,:precision) as geomInt
        FROM ref_departement d
        inner join ref_departement_geometry dg on d.id=dg.id
        where d.id_region in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findInListeRegions(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);
}

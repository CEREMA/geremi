package fr.cerema.dsi.geremi.repositories;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Commune;

/**
 * Interface de répository pour les opérations CRUD sur les objets Commune.
 * Cette interface hérite de l'interface générique {@link fr.cerema.dsi.commons.repositories.GenericRepository}
 * pour hériter de ses méthodes de base.
 */
public interface CommuneRepository extends GenericRepository<Commune, String> {


	/**
	 * Méthode pour trouver les objets Commune qui se trouvent dans une boîte englobante donnée.
	 *
	 * @param bbox0 latitude minimale de la boîte englobante
	 * @param bbox1 longitude minimale de la boîte englobante
	 * @param bbox2 latitude maximale de la boîte englobante
	 * @param bbox3 longitude maximale de la boîte englobante
	 *
	 * @return une liste d'objets Commune qui se trouvent dans la boîte englobante donnée.
	 */
	@Query(value="""
                SELECT c.id as id, c.nom_commune as nom, c.insee_commune as code, public.ST_REDUCEPRECISION(g.the_geom,:precision) as geomInt
                FROM ref_commune c
                INNER JOIN ref_commune_geometry g ON c.id=g.id
                WHERE public.ST_INTERSECTS(public.ST_MakeEnvelope(:bbox0, :bbox1, :bbox2, :bbox3,4326), g.the_geom)
                """,nativeQuery = true)
	List<SelectionZonage> findInBox(@Param("bbox0") double  bbox0, @Param("bbox1") double  bbox1, @Param("bbox2") double  bbox2, @Param("bbox3") double  bbox3, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        inner join ref_departement d on c.id_departement=d.id
        where d.id_region in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneInRegion(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        where c.id_departement in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneInDepartement(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        where c.id_zone_emploi in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneInZoneEmploi(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        where c.id_bassin_vie in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneInBassinVie(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        where c.id_epci in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneInEPCI(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

  @Query(value="""
        SELECT c.id as id,
        c.nom_commune as nom,
        c.insee_commune as code,
        public.ST_REDUCEPRECISION(cg.the_geom,:precision) as geomInt
        FROM ref_commune c
        inner join ref_commune_geometry cg on c.id=cg.id
        where c.id in :liste_id
        """,nativeQuery=true)
  List<SelectionZonage> findCommuneByIds(@Param("liste_id") List<Long> liste_id, @Param("precision") BigDecimal precision);

}

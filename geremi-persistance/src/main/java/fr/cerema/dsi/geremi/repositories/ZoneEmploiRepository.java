package fr.cerema.dsi.geremi.repositories;

import java.math.BigDecimal;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ZoneEmploi;

/**
 * Interface de répository pour les opérations CRUD sur les objets Zone Emploi.
 *
 * Cette interface hérite de l'interface générique {@link fr.cerema.dsi.commons.repositories.GenericRepository}
 * pour hériter de ses méthodes de base.
 */
public interface ZoneEmploiRepository extends GenericRepository<ZoneEmploi,String> {


	/**
	 * Méthode pour trouver les objets ZoneEmploi qui se trouvent dans une boîte englobante donnée.
	 *
	 * @param bbox0 latitude minimale de la boîte englobante
	 * @param bbox1 longitude minimale de la boîte englobante
	 * @param bbox2 latitude maximale de la boîte englobante
	 * @param bbox3 longitude maximale de la boîte englobante
	 *
	 * @return une liste d'objets ZoneEmploi qui se trouvent dans la boîte englobante donnée.
	 */
	 @Query(value="""
                SELECT r.id as id, r.nom_zone_emploi as nom, r.code_zone_emploi as code, public.ST_REDUCEPRECISION(g.the_geom,:precision) as geomInt
                FROM ref_zone_emploi r
                INNER JOIN ref_zone_emploi_geometry g ON r.id=g.id
                WHERE  public.ST_INTERSECTS( public.ST_MakeEnvelope(:bbox0, :bbox1, :bbox2, :bbox3,4326), g.the_geom)
                """,nativeQuery = true)
	List<SelectionZonage> findInBox(@Param("bbox0") double  bbox0, @Param("bbox1") double  bbox1, @Param("bbox2") double  bbox2, @Param("bbox3") double  bbox3, @Param("precision") BigDecimal precision);

  @Query(value="""
                SELECT DISTINCT g.id,code_zone_emploi as code,nom_zone_emploi as nom,
                public.ST_CollectionExtract(public.ST_REDUCEPRECISION(public.ST_INTERSECTION(the_geom, union_region_geom.geom),:precision)) as geomInt,
                public.ST_CollectionExtract(public.ST_REDUCEPRECISION(public.ST_Difference(the_geom, union_region_geom.geom),:precision)) as geomExt,
                not public.ST_IsEmpty(public.ST_Difference(the_geom, union_region_geom.geom)) as exterieur
                FROM (
                    select public.ST_Union(the_geom) as geom from ref_region_geometry where id in :regionId
                ) as union_region_geom
                cross join ref_zone_emploi d
                join ref_zone_emploi_geometry g ON d.id=g.id
                where
                  public.ST_Intersects(g.the_geom, union_region_geom.geom)
                  and public.ST_AREA(public.ST_REDUCEPRECISION(public.ST_INTERSECTION(the_geom, union_region_geom.geom),0.000001)) > 0
                """,nativeQuery=true)
  List<SelectionZonage> selectionInterieurExterieurZoneEmploiInRegion(@Param("regionId") List<Long> regionId, BigDecimal precision);

  @Query(value="""
                SELECT DISTINCT g.id,code_zone_emploi as code,nom_zone_emploi as nom,
                public.ST_CollectionExtract(public.ST_REDUCEPRECISION(public.ST_INTERSECTION(the_geom, union_departement_geom.geom),:precision)) as geomInt,
                public.ST_CollectionExtract(public.ST_REDUCEPRECISION(public.ST_Difference(the_geom, union_departement_geom.geom),:precision)) as geomExt,
                not public.ST_IsEmpty(public.ST_Difference(the_geom, union_departement_geom.geom)) as exterieur
                FROM (
                    select public.ST_Union(the_geom) as geom from ref_departement_geometry where id in :departementId
                ) as union_departement_geom
                cross join ref_zone_emploi d
                join ref_zone_emploi_geometry g ON d.id=g.id
                where
                  public.ST_Intersects(g.the_geom, union_departement_geom.geom)
                  and public.ST_AREA(public.ST_REDUCEPRECISION(public.ST_INTERSECTION(the_geom, union_departement_geom.geom),0.000001)) > 0
                """,nativeQuery=true)
  List<SelectionZonage> selectionInterieurExterieurZoneEmploiInDepartement(@Param("departementId") List<Long> departementId, BigDecimal precision);



}

package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Zone;

public interface ZoneRepository extends GenericRepository<Zone, Long> {

  @Query(value = """
                  with territoire_srid as (
                      select t.the_geom as geom, min(dg.srid) as srid from territoire t
                      join ref_departement_geometry dg on public.st_intersects(t.the_geom, dg.the_geom)
                      where t.id_etude=:id_etude
                      group by t.the_geom
                  )
                  select public.St_Area(public.st_transform(public.ST_Difference(ts.geom, rp.the_geom),srid)) / public.St_Area(public.st_transform(ts.geom,srid)) * 100
                  from territoire_srid ts join ref_pays rp on rp.id_pays=1
                  """, nativeQuery = true)
  Double findTauxZoneHorsFrance(@Param("id_etude") Long id_etude);

  @Query(value = """
                  with region_srid as (
                      select rg.the_geom as geom, min(dg.srid) as srid from ref_region_geometry rg
                      join ref_departement_geometry dg on public.st_intersects(rg.the_geom, dg.the_geom)
                      where rg.id=:id_region
                      group by rg.the_geom
                  )
                  select public.St_Area(public.st_transform(public.ST_Difference(t.the_geom, rs.geom),srid)) / public.St_Area(public.st_transform(t.the_geom,srid)) * 100
                  from region_srid rs join territoire t on id_etude=:id_etude
                  """, nativeQuery = true)
  Double findTauxZoneHorsRegion(@Param("id_etude") Long id_etude,
                            @Param("id_region") Long id_region);

  @Query(value = """
                  with region_srid as (
                      select rg.the_geom as geom, min(dg.srid) as srid from ref_region_geometry rg
                      join ref_departement_geometry dg on public.st_intersects(rg.the_geom, dg.the_geom)
                      where rg.id=:id_region
                      group by rg.the_geom
                  ),
                  union_zone as (
                      select public.ST_Union(the_geom) as geom from zone where id_etude=:id_etude
                      )
                  select public.St_Area(public.st_transform(public.ST_INTERSECTION(rs.geom, uz.geom),srid)) / public.St_Area(public.st_transform(rs.geom,srid)) * 100
                  from region_srid rs
                  cross join union_zone uz
                  """, nativeQuery = true)
  Double findTauxCouvertureRegion(@Param("id_etude") Long id_etude,
                                  @Param("id_region") Long id_region);

  @Query(value="SELECT z.id_zone,z.type,z.nom,z.description,z.code,public.ST_ReducePrecision(z.the_geom,:precision) as the_geom,z.id_etude FROM zone z where z.id_etude=:id",nativeQuery = true)
  List<Zone> findByEtude(Long id, double precision);

  @Query(value="SELECT z.id_zone,z.type,z.nom, z.code, z.description, z.the_geom,z.id_etude FROM zone z JOIN etude e ON z.id_etude = e.id_etude WHERE z.id_etude=:id",nativeQuery = true)
  List<Zone> findZoneByEtude(Long id);

  @Query(value="SELECT z.id_zone FROM {h-schema}zone z WHERE z.id_etude=:idEtude",nativeQuery = true)
  List<Long> listZoneIdByEtudeId(@Param("idEtude") Long idEtude);

  @Query(value="SELECT z.id_zone,z.type,z.nom, z.code, z.description, public.ST_ReducePrecision(public.ST_Difference((z.the_geom), (select the_geom from ref_region_geometry as r where r.id=:id_region)),:precision) the_geom,z.id_etude FROM zone z JOIN etude e ON z.id_etude = e.id_etude WHERE z.id_etude=:id and public.ST_AREA(public.ST_REDUCEPRECISION(public.ST_INTERSECTION((the_geom), (SELECT the_geom FROM ref_region_geometry r WHERE r.id=:id_region)),0.000001)) > 0",nativeQuery = true)
  List<Zone> findZoneByEtudeExterieurRegion(@Param("id") Long id,
                                            @Param("id_region") Long idRegion,
                                            @Param("precision") double precision);

  @Query(value="SELECT z.id_zone,z.type,z.nom, z.code, z.description, public.ST_ReducePrecision(public.ST_Difference(the_geom, (select the_geom from ref_pays where id_pays=1)),:precision) the_geom, z.id_etude FROM zone z JOIN etude e ON z.id_etude = e.id_etude WHERE z.id_etude=:id and public.ST_IsEmpty(public.ST_Difference((the_geom), public.ST_Difference(the_geom, (select the_geom from ref_pays where id_pays=1)))) = false",nativeQuery = true)
  List<Zone> findZoneByEtudeExterieurFrance(@Param("id") Long id,
                                            @Param("precision") double precision);

  @Query(value="SELECT z.id_zone,z.type,z.nom, z.code, z.description, z.the_geom, z.id_etude FROM zone z  WHERE z.code=:code and z.id_etude=:id",nativeQuery = true)
  Zone findZoneByCode(String code, Long id);

  @Query(value="SELECT z.id_zone, z.type, z.nom, z.code, z.description, z.the_geom, z.id_etude FROM zone z WHERE z.id_etude=:id",nativeQuery = true)
  List<Zone> findZonesByCode(Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) SELECT :type_zone, nom_departement, :description, insee_departement, the_geom, :id_etude FROM ref_departement d INNER JOIN ref_departement_geometry g ON d.id=g.id WHERE d.id_region in :liste_id", nativeQuery = true)
  void createZoneDepartement(@Param("type_zone") String typeZone,
                             @Param("description") String description,
                             @Param("id_etude") Long idEtude,
                             @Param("liste_id") List<Long> listeRegion);


  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) SELECT :type_zone, nom_zone_emploi, :description, code_zone_emploi, public.ST_multi(public.ST_Difference((g.the_geom), public.ST_Difference(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire)))), :id_etude FROM ref_zone_emploi d INNER JOIN ref_zone_emploi_geometry g ON d.id=g.id where public.ST_Intersects(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire))", nativeQuery = true)
  void createZoneZoneEmploi(@Param("type_zone") String type_zone,
                                  @Param("description") String description,
                                  @Param("id_etude") Long id_etude,
                                  @Param("id_territoire") Long id_territoire);

  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) SELECT :type_zone, nom_bassin_vie, :description, code_bassin_vie, public.ST_multi(public.ST_Difference((g.the_geom), public.ST_Difference(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire)))), :id_etude FROM ref_bassin_vie d INNER JOIN ref_bassin_vie_geometry g ON d.id=g.id where public.ST_Intersects(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire))", nativeQuery = true)
  void createZoneBassinVie(@Param("type_zone") String type_zone,
                                 @Param("description") String description,
                                 @Param("id_etude") Long id_etude,
                                 @Param("id_territoire") Long id_territoire);

  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) SELECT :type_zone, nom_epci, :description, siren_epci, public.ST_multi(public.ST_Difference((g.the_geom), public.ST_Difference(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire)))), :id_etude FROM ref_epci d INNER JOIN ref_epci_geometry g ON d.id=g.id where public.ST_Intersects(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire))", nativeQuery = true)
  void createZoneEPCI(@Param("type_zone") String type_zone,
                            @Param("description") String description,
                            @Param("id_etude") Long id_etude,
                            @Param("id_territoire") Long id_territoire);

  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) SELECT :type_zone, nom_commune, :description, insee_commune, public.ST_multi(public.ST_Difference((g.the_geom), public.ST_Difference(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire)))), :id_etude FROM ref_commune d INNER JOIN ref_commune_geometry g ON d.id=g.id where public.ST_Intersects(g.the_geom, (SELECT the_geom FROM territoire WHERE id_territoire=:id_territoire))", nativeQuery = true)
  void createZoneCommune(@Param("type_zone") String typeZone,
                         @Param("description") String description,
                         @Param("id_etude") Long idEtude,
                         @Param("id_territoire") Long idTerritoire);

  @Modifying(clearAutomatically = true)
  @Query(value = "INSERT INTO zone(type,nom,description,code,the_geom,id_etude) VALUES (:type_zone, :nom, :description, :code_zone, :the_geom, :id_etude)", nativeQuery = true)
  void createZonePerso(@Param("type_zone") String typeZone,
                       @Param("nom") String nom,
                       @Param("description") String description,
                       @Param("id_etude") Long idEtude,
                       @Param("the_geom") Geometry geometry,
                       @Param("code_zone") String codeZone);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM zone WHERE id_etude=:id_etude", nativeQuery = true)
  void deleteAllFromEtude(@Param("id_etude") Long idEtude);

  @Query(value = "UPDATE zone SET the_geom = (SELECT public.ST_multi(public.ST_INTERSECTION(the_geom, (select the_geom from ref_region_geometry as r WHERE r.id=:id_region))) from zone WHERE id_zone=:id) WHERE id_zone=:id RETURNING id_zone", nativeQuery = true)
  void updateTerritoireInRegion(@Param("id") Long id,
                                @Param("id_region") Long idRegion);
  @Query(value = "UPDATE zone SET the_geom = (SELECT public.ST_multi(public.ST_INTERSECTION(the_geom, (select the_geom from ref_pays where id_pays=1))) from zone WHERE id_zone=:id) WHERE id_zone=:id RETURNING id_zone", nativeQuery = true)
  void updateTerritoireInFrance(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM zone WHERE id_etude=:id_etude and public.ST_isEmpty(the_geom) = true", nativeQuery = true)
  void cleanZoneVideIdEtude(@Param("id_etude") Long idEtude);

  @Query(value="SELECT z.nom FROM zone z WHERE z.id_zone IN :ids", nativeQuery = true)
  List<String> getZoneNamesByIds(@Param("ids") List<Long> ids);

}


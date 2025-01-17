package fr.cerema.dsi.geremi.repositories;


import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Territoire;

public interface TerritoireRepository extends GenericRepository<Territoire, Long> {

  Territoire findByEtude(Etude etude);

  Territoire findByEtude_Id(Long idEtude);

  @Query(value = "select distinct z.type FROM {h-schema}zone z WHERE z.id_etude = :id_etude", nativeQuery = true)
  Optional<String> findTypeZonageByIdEtude(@Param("id_etude") Long idEtude);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_region_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireRegion(@Param("type_territoire") String type_territoire,
                              @Param("nom") String nom,
                              @Param("description") String description,
                              @Param("liste_territoire") String listeTerritoire,
                              @Param("id_etude") Long id_etude,
                              @Param("liste_id") List<Long> liste_id);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_departement_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireDepartement(@Param("type_territoire") String type_territoire,
                                   @Param("nom") String nom,
                                   @Param("description") String description,
                                   @Param("liste_territoire") String listeTerritoire,
                                   @Param("id_etude") Long id_etude,
                                   @Param("liste_id") List<Long> liste_id);
    @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_zone_emploi_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireZoneEmploi(@Param("type_territoire") String type_territoire,
                                  @Param("nom") String nom,
                                  @Param("description") String description,
                                  @Param("liste_territoire") String listeTerritoire,
                                  @Param("id_etude") Long id_etude,
                                  @Param("liste_id") List<Long> liste_id);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_bassin_vie_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireBassinVie(@Param("type_territoire") String type_territoire,
                                 @Param("nom") String nom,
                                 @Param("description") String description,
                                 @Param("liste_territoire") String listeTerritoire,
                                 @Param("id_etude") Long id_etude,
                                 @Param("liste_id") List<Long> liste_id);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_epci_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireEPCI(@Param("type_territoire") String type_territoire,
                            @Param("nom") String nom,
                            @Param("description") String description,
                            @Param("liste_territoire") String listeTerritoire,
                            @Param("id_etude") Long id_etude,
                            @Param("liste_id") List<Long> liste_id);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,liste_territoire,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, :liste_territoire, (SELECT public.ST_multi(public.ST_UNION(the_geom)) FROM ref_commune_geometry WHERE id in :liste_id), :id_etude) RETURNING id_territoire", nativeQuery = true)
  void createTerritoireCommune(@Param("type_territoire") String type_territoire,
                               @Param("nom") String nom,
                               @Param("description") String description,
                               @Param("liste_territoire") String listeTerritoire,
                               @Param("id_etude") Long id_etude,
                               @Param("liste_id") List<Long> liste_id);

  @Query(value = "INSERT INTO {h-schema}territoire(type,nom,description,the_geom,id_etude) VALUES (:type_territoire, :nom, :description, (SELECT public.ST_multi(public.ST_UNION(the_geom)) as the_geom FROM zone z WHERE z.id_etude=:id_etude), :id_etude) RETURNING id_territoire", nativeQuery = true)
  Long createTerritoireFromImport(@Param("type_territoire") String type_territoire,
                                  @Param("nom") String nom,
                                  @Param("description") String description,
                                  @Param("id_etude") Long id_etude);

  @Query(value = "SELECT id_territoire, type, nom, description, liste_territoire, public.ST_ReducePrecision(the_geom,:precision) as the_geom, id_etude FROM {h-schema}territoire WHERE id_territoire=:id", nativeQuery = true)
  Territoire findByIdWithPrecision(@Param("id") Long id,
                                   @Param("precision") double precision);
  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}territoire WHERE id_etude=:id_etude", nativeQuery = true)
  void deleteByIdEtude(@Param("id_etude") Long idEtude);


  @Query(value = "UPDATE {h-schema}territoire SET nom = :nom, description = :description, the_geom = (SELECT public.ST_multi(public.ST_Union(the_geom)) from zone WHERE id_etude=:id_etude) WHERE id_etude=:id_etude RETURNING id_territoire", nativeQuery = true)
  void updateTerritoireInRegion(@Param("nom") String nom,
                                @Param("description") String description,
                                @Param("id_etude") Long idEtude);

  @Query(value = "UPDATE {h-schema}territoire SET nom = :nom, description = :description, the_geom = (SELECT public.ST_multi(public.ST_Union(the_geom)) from zone WHERE id_etude=:id_etude) WHERE id_etude=:id_etude RETURNING id_territoire", nativeQuery = true)
  void updateTerritoireInFrance(@Param("nom") String nom,
                                @Param("description") String description,
                                @Param("id_etude") Long idEtude);

  @Query(value = "SELECT public.ST_CONTAINS(t.the_geom,:geometry) FROM {h-schema}territoire t WHERE t.id_etude=:idEtude",nativeQuery = true)
  boolean isInTerritoire(@Param("geometry") Geometry geometry,
                         @Param("idEtude") Long idEtude);

  @Query(value = "SELECT public.ST_Intersects(public.ST_SetSRID(t.the_geom,4326), :geometry) FROM {h-schema}territoire t WHERE t.id_etude=:idEtude", nativeQuery = true)
  boolean intersectsTerritoire(@Param("geometry") Geometry geometry, @Param("idEtude") Long idEtude);

}


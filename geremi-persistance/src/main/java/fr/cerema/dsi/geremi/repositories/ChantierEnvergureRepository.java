package fr.cerema.dsi.geremi.repositories;


import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ChantierEnvergure;

public interface ChantierEnvergureRepository extends GenericRepository<ChantierEnvergure, Integer> {

  @Query(value = """
                 SELECT c.id_chantier,c.nom,c.description,c.annee_debut,c.annee_fin,c.beton_pref,c.viab_autre,c.ton_tot,c.the_geom,c.id_source,c.id_frere,c.id_etude
                 FROM {h-schema}chantier_envergure c
                 JOIN {h-schema}etude etude_chantier ON c.id_etude = etude_chantier.id_etude
                 JOIN {h-schema}rel_scenario_chantier rsc ON c.id_chantier = rsc.id_chantier
                 JOIN {h-schema}scenario s ON s.id_scenario = rsc.id_scenario AND s.id_etude = etude_chantier.id_etude AND s.if_retenu = TRUE
                 JOIN {h-schema}territoire t on public.ST_Intersects(c.the_geom,  t.the_geom) and t.id_territoire = :idTerritoire
                 JOIN {h-schema}etude etude_territoire ON t.id_etude = etude_territoire.id_etude
                 WHERE etude_chantier.if_public = TRUE
                 AND c.annee_debut <= etude_territoire.annee_fin
                 AND c.annee_fin >= etude_territoire.annee_ref
                 AND etude_territoire.id_etude != c.id_etude
                 """, nativeQuery = true)	List<ChantierEnvergure> getChantierEnvergureByIdTerritoire(
			@Param("idTerritoire") Long idTerritoire);

  @Query(value = "SELECT c.id_chantier,c.nom,c.description,c.annee_debut,c.annee_fin,c.beton_pref,c.viab_autre,c.ton_tot,c.the_geom,c.id_source,c.id_frere,c.id_etude FROM {h-schema}chantier_envergure c INNER JOIN {h-schema}etude e ON c.id_etude = e.id_etude WHERE c.id_etude = :idEtude AND c.annee_debut <= e.annee_fin AND c.annee_fin >= e.annee_ref", nativeQuery = true)
	List<ChantierEnvergure> getChantierByEtude(@Param("idEtude") Long idEtude);

  @Query(value = """
                 SELECT c.id_chantier,c.nom,c.description,c.annee_debut,c.annee_fin,c.beton_pref,c.viab_autre,c.ton_tot,c.the_geom,c.id_source,c.id_frere,c.id_etude
                 FROM {h-schema}chantier_envergure c
                 JOIN {h-schema}etude etude_chantier ON c.id_etude = etude_chantier.id_etude
                 JOIN {h-schema}rel_scenario_chantier rsc ON c.id_chantier = rsc.id_chantier
                 JOIN {h-schema}scenario s ON s.id_scenario = rsc.id_scenario AND s.id_etude = etude_chantier.id_etude AND s.if_retenu = TRUE
                 JOIN {h-schema}territoire t on public.ST_Intersects(c.the_geom,  t.the_geom) and t.id_territoire = :idTerritoire
                 JOIN {h-schema}etude etude_territoire ON t.id_etude = etude_territoire.id_etude
                 WHERE etude_chantier.if_public = TRUE
                 AND c.annee_debut <= etude_territoire.annee_fin
                 AND c.annee_fin >= etude_territoire.annee_ref
                 AND etude_territoire.id_etude != c.id_etude
                 AND c.id_chantier not in :liste_id
                 """, nativeQuery = true)
  List<ChantierEnvergure> getExistingChantiersByIdTerritoire(@Param("idTerritoire") Long idTerritoire,
                                                             @Param("liste_id") List<Integer> liste_id);

	@Query(value = "INSERT INTO {h-schema}chantier_envergure(nom, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, id_source, id_frere, id_etude) VALUES (:nom, :description, :annee_debut, :annee_fin, :beton_pref, :viab_autre, :ton_tot, :the_geom, :id_source, :id_frere, :id_etude) RETURNING id_chantier", nativeQuery = true)
  Integer createChantierEnvergure(@Param("nom") String nom, @Param("description") String description,
			@Param("annee_debut") Integer anneeDebut, @Param("annee_fin") Integer anneeFin,
            @Param("beton_pref") Long betonPref, @Param("viab_autre") Long viabAutre,
            @Param("ton_tot") Long tonTot, @Param("the_geom") Geometry geometry,
            @Param("id_source") Integer idSource, @Param("id_frere") Integer idFrere,
            @Param("id_etude") Long idEtude);

  @Query(value = "INSERT INTO {h-schema}chantier_envergure(nom, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, id_source, id_frere, id_etude) SELECT nom, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, :idSource, :idFrere, :idEtude FROM {h-schema}chantier_envergure WHERE id_chantier = :idChantier RETURNING id_chantier", nativeQuery = true)
  Integer duplicateChantierEnvergureExistante(@Param("idChantier") Integer idChantier,
                                              @Param("idSource") Integer idSource,
                                              @Param("idFrere") Integer idFrere,
                                              @Param("idEtude") Long idEtude);

  @Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM {h-schema}chantier_envergure WHERE id_chantier = :idChantier", nativeQuery = true)
	void deleteChantierEnvergure(@Param("idChantier") Integer idChantier);

  @Query(value = "SELECT public.ST_Intersects(t.the_geom,(SELECT the_geom c FROM {h-schema}chantier_envergure c WHERE id_chantier=:idChantier)) FROM {h-schema}territoire t WHERE t.id_etude=:idEtude",nativeQuery = true)
  boolean isInTerritoire(@Param("idChantier") Integer idChantier,
                         @Param("idEtude") Long idEtude);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}chantier_envergure WHERE id_etude = :idEtude", nativeQuery = true)
  void deleteByIdEtude(@Param("idEtude") Long idEtude);
}

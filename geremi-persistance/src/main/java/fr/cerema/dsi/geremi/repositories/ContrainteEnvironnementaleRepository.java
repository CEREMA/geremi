package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;

public interface ContrainteEnvironnementaleRepository extends GenericRepository<ContrainteEnvironnementale, Integer> {

	@Query(value = """
                  SELECT c.id_contr_env,c.nom,c.description,c.niveau,c.the_geom,c.id_source,c.id_etude
                  FROM {h-schema}contrainte_environnementale c
                  JOIN {h-schema}etude etude_contrainte ON c.id_etude = etude_contrainte.id_etude
                  JOIN {h-schema}rel_scenario_contrainte rsc ON c.id_contr_env = rsc.id_contrainte
                  JOIN {h-schema}scenario s ON s.id_scenario = rsc.id_scenario AND s.id_etude = etude_contrainte.id_etude AND s.if_retenu = TRUE
                  JOIN {h-schema}territoire t on public.ST_Intersects(c.the_geom, t.the_geom) and t.id_territoire = :idTerritoire
                  JOIN {h-schema}etude etude_territoire on etude_territoire.id_etude = t.id_etude
                  WHERE etude_contrainte.if_src = TRUE
                  AND etude_contrainte.if_public = TRUE
                  AND c.id_source IS NULL
                  AND etude_territoire.id_etude != c.id_etude
                  """, nativeQuery = true)
	List<ContrainteEnvironnementale> getContrainteEnvironnementaleByIdTerritoire(
			@Param("idTerritoire") Long idTerritoire);

	@Query(value = "SELECT * FROM {h-schema}contrainte_environnementale c WHERE c.id_etude = :idEtude", nativeQuery = true)
	List<ContrainteEnvironnementale> getContrainteByEtude(@Param("idEtude") Long idEtude);

	@Query(value = "INSERT INTO {h-schema}contrainte_environnementale(nom, description, niveau, the_geom, id_source, id_etude) VALUES (:nom, :description, :niveau, :the_geom, :id_source, :id_etude) RETURNING id_contr_env", nativeQuery = true)
  Integer createContrainteEnvironnementale(@Param("nom") String nom, @Param("description") String description,
			@Param("niveau") String niveau, @Param("the_geom") Geometry geometry, @Param("id_source") Integer idSource,
			@Param("id_etude") Long idEtude);

	@Query(value = """
                  SELECT c.id_contr_env,c.nom,c.description,c.niveau,c.the_geom,c.id_source,c.id_etude
                  FROM {h-schema}contrainte_environnementale c
                  JOIN {h-schema}etude etude_contrainte ON c.id_etude = etude_contrainte.id_etude
                  JOIN {h-schema}rel_scenario_contrainte rsc ON c.id_contr_env = rsc.id_contrainte
                  JOIN {h-schema}scenario s ON s.id_scenario = rsc.id_scenario AND s.id_etude = etude_contrainte.id_etude AND s.if_retenu = TRUE
                  JOIN {h-schema}territoire t on public.ST_Intersects(c.the_geom, t.the_geom) and t.id_territoire = :idTerritoire
                  JOIN {h-schema}etude etude_territoire on etude_territoire.id_etude = t.id_etude
                  WHERE etude_contrainte.if_src = TRUE
                  AND etude_contrainte.if_public = TRUE
                  AND c.id_source IS NULL
                  AND etude_territoire.id_etude != c.id_etude
                  AND c.id_contr_env not in :liste_id
                  """, nativeQuery = true)
	List<ContrainteEnvironnementale> getExistingContraintesByIdTerritoire(@Param("idTerritoire") Long idTerritoire,
                                                                        @Param("liste_id") List<Integer> liste_id);


	@Query(value = "INSERT INTO {h-schema}contrainte_environnementale(nom, description, niveau, the_geom, id_source, id_etude) SELECT nom, description, niveau, the_geom, :idContrEnv, :idEtude FROM {h-schema}contrainte_environnementale WHERE id_contr_env = :idContrEnv RETURNING id_contr_env", nativeQuery = true)
  	Integer duplicateContrainteEnvironnementaleExistante(@Param("idContrEnv") Integer idContrEnv, @Param("idEtude") Long idEtude);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM {h-schema}contrainte_environnementale WHERE id_contr_env = :idContrEnv", nativeQuery = true)
	void deleteContrainteEnvironnementale(@Param("idContrEnv") Integer idContrEnv);

  @Query(value = "SELECT public.ST_Intersects(t.the_geom,(SELECT the_geom c FROM {h-schema}contrainte_environnementale c WHERE id_contr_env=:idContrEnv)) FROM {h-schema}territoire t WHERE t.id_etude=:idEtude",nativeQuery = true)
  boolean isInTerritoire(@Param("idContrEnv") Integer idContrEnv,
                                   @Param("idEtude") Long idEtude);

  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM {h-schema}contrainte_environnementale WHERE id_etude = :idEtude", nativeQuery = true)
  void deleteByIdEtude(@Param("idEtude") Long idEtude);
}

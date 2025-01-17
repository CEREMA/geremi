package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.InstallationStockage;

public interface InstallationStockageRepository extends GenericRepository<InstallationStockage, Integer> {

    @Query(value = """
                    SELECT i.id_stockage, i.nom_etab, i.code_etab, i.description, i.annee_debut, i.annee_fin, i.beton_pref, i.viab_autre, i.ton_tot, i.the_geom, i.id_source, i.id_frere, i.id_etude
                    FROM {h-schema}installation_stockage i
                    JOIN {h-schema}etude etude_installation ON i.id_etude = etude_installation.id_etude
                    JOIN {h-schema}rel_scenario_installation rsi ON i.id_stockage = rsi.id_installation
                    JOIN {h-schema}scenario s ON s.id_scenario = rsi.id_scenario AND s.id_etude = etude_installation.id_etude AND s.if_retenu = TRUE
                    JOIN {h-schema}territoire t on public.ST_Intersects(i.the_geom,  t.the_geom) and t.id_territoire = :idTerritoire
                    JOIN {h-schema}etude etude_territoire ON t.id_etude = etude_territoire.id_etude
                    WHERE etude_installation.if_public = TRUE
                    AND i.annee_debut <= etude_territoire.annee_fin
                    AND i.annee_fin >= etude_territoire.annee_ref
                    AND etude_territoire.id_etude != i.id_etude
                    """, nativeQuery = true)
    List<InstallationStockage> getInstallationStockageByIdTerritoire(@Param("idTerritoire") Long idTerritoire);

    @Query(value = "SELECT  i.id_stockage, i.nom_etab, i.code_etab, i.description, i.annee_debut, i.annee_fin, i.beton_pref, i.viab_autre, i.ton_tot, i.the_geom, i.id_source, i.id_frere, i.id_etude FROM {h-schema}installation_stockage i INNER JOIN {h-schema}etude e ON i.id_etude = e.id_etude WHERE i.id_etude = :idEtude AND i.annee_debut <= e.annee_fin AND i.annee_fin >= e.annee_ref", nativeQuery = true)
    List<InstallationStockage> getInstallationByEtude(@Param("idEtude") Long idEtude);

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO {h-schema}installation_stockage(nom_etab, code_etab, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, id_source, id_frere, id_etude) VALUES (:nomEtab, :codeEtab, :description, :anneeDebut, :anneeFin, :betonPref, :viabAutre, :tonTot, :theGeom, :idSource, :idFrere, :idEtude)", nativeQuery = true)
    void createInstallationStockage(@Param("nomEtab") String nomEtab, @Param("codeEtab") String codeEtab, @Param("description") String description, @Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin, @Param("betonPref") Long betonPref, @Param("viabAutre") Long viabAutre, @Param("tonTot") Long tonTot, @Param("theGeom") Geometry geometry, @Param("idSource") Integer idSource, @Param("idFrere") Integer idFrere, @Param("idEtude") Long idEtude);

  @Query(value = """
                    SELECT i.id_stockage, i.nom_etab, i.code_etab, i.description, i.annee_debut, i.annee_fin, i.beton_pref, i.viab_autre, i.ton_tot, i.the_geom, i.id_source, i.id_frere, i.id_etude
                    FROM {h-schema}installation_stockage i
                    JOIN {h-schema}etude etude_installation ON i.id_etude = etude_installation.id_etude
                    JOIN {h-schema}rel_scenario_installation rsi ON i.id_stockage = rsi.id_installation
                    JOIN {h-schema}scenario s ON s.id_scenario = rsi.id_scenario AND s.id_etude = etude_installation.id_etude AND s.if_retenu = TRUE
                    JOIN {h-schema}territoire t on public.ST_Intersects(i.the_geom,  t.the_geom) and t.id_territoire = :idTerritoire
                    JOIN {h-schema}etude etude_territoire ON t.id_etude = etude_territoire.id_etude
                    WHERE etude_installation.if_public = TRUE
                    AND i.annee_debut <= etude_territoire.annee_fin
                    AND i.annee_fin >= etude_territoire.annee_ref
                    AND etude_territoire.id_etude != i.id_etude
                    AND i.id_stockage not in :liste_id
                    """, nativeQuery = true)
    List<InstallationStockage> getExistingInstallationStockageByIdTerritoire(@Param("idTerritoire") Long idTerritoire,
                                                                             @Param("liste_id") List<Integer> liste_id);

    @Query(value = "INSERT INTO {h-schema}installation_stockage(nom_etab, code_etab, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, id_source, id_frere, id_etude) SELECT nom_etab, code_etab, description, annee_debut, annee_fin, beton_pref, viab_autre, ton_tot, the_geom, :idSource, :idFrere, :idEtude FROM {h-schema}installation_stockage WHERE id_stockage = :idStockage RETURNING id_stockage", nativeQuery = true)
    Integer duplicateInstallationStockageExistante(@Param("idStockage") Integer idStockage,
                                                   @Param("idSource") Integer idSource,
                                                   @Param("idFrere") Integer idFrere,
                                                   @Param("idEtude") Long idEtude);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM {h-schema}installation_stockage WHERE id_stockage = :idStockage", nativeQuery = true)
    void deleteInstallationStockage(@Param("idStockage") Integer idStockage);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE {h-schema}installation_stockage SET nom_etab = :nomEtab, description = :description, annee_debut = :anneeDebut, annee_fin = :anneeFin, beton_pref = :betonPref, viab_autre = :viabAutre, ton_tot = :tonTot WHERE id_stockage = :idStockage", nativeQuery = true)
    void updateInstallationStockageById(@Param("idStockage") Integer idStockage, @Param("nomEtab") String nomEtab, @Param("description") String description, @Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin, @Param("betonPref") Integer betonPref, @Param("viabAutre") Integer viabAutre, @Param("tonTot") Integer tonTot);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM {h-schema}installation_stockage WHERE id_etude = :idEtude", nativeQuery = true)
    void deleteByIdEtude(@Param("idEtude") Long idEtude);
}

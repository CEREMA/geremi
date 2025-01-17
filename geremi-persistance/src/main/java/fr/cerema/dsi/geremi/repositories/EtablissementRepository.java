package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import java.util.Map;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Etablissement;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface EtablissementRepository extends GenericRepository<Etablissement, String> {

	/**
	 * Requête pour récupérer les Etablissements par rapport à une année, et un type de profil
   * des filtres supplémentaires sont présents pour filtrer selon
   *  - l'origineMat
   *  - ne récupérer que les établissements liés à une étude (dans le territoire ou exportant vers celui ci
   *
   * Les champs retournés sont filtrés en fonction des droits paramétrés dans la table metadonnées selon la sémantique suivante
   *  - si le champ est paramétré if_public = true, il est retourné pour tous les types d'utilisateurs
   *  - si le champ est paramétré if_public = false, il est retourné uniquement pour les utilisateurs NON public
   *  - si le champ est paramétré if_public = null, il n'est pas retourné par la requête
	 *
   * Une jointure est réalisée avec les tables traitement_dechet et destination pour récupérer les champs correspondants
   *
	 * @param annee année de recherche
	 * @param origineMat filtre sur origineMat (optionnel)
	 * @param ifPublic boolean indiquant s'il faut restreindre aux champs public
	 * @param idEtude filtre sur l'étude (optionnel)
	 * @return la liste des Etablissements trouvés,
	 */
  @Query(value = """
                  with etablissement_annee as (
                      select * from {h-schema}etablissement e
                      where make_date(CAST (:annee AS INTEGER),1,1) between e.date_debut and e.date_fin
                  ),
                  etablissement_filtre as(
                      select id_tech, id_etab, code_etab, annee, nom_etablissement, siret, region, departement, libelle_adresse, code_insee_commune, libelle_commune, code_ape, activite_principale, long, lat, volume_production, unite, type_produit, nb_employe, site_internet, the_geom, service_inspection, if_carriere, if_quota, origin_mat, max_production_annuelle_autorisee, moy_production_annuelle_autorisee, to_char(date_fin_autorisation,'DD/MM/YYYY') as date_fin_autorisation, type_carriere, to_char(date_debut,'DD/MM/YYYY') as date_debut, to_char(date_fin,'DD/MM/YYYY') as date_fin, to_char(date_maj, 'DD/MM/YYYY HH24:MI:SS:MS') as date_maj, code_postal_site
                      from (
                          select id_etab as id_tech, jsonb_object_agg(fe.key, fe.value) ag
                          from (
                              select id_etab, key,
                                  case if_public
                                      when :ifPublic then value
                                      when true then value
                                      end as value
                              from (
                                  SELECT id_etab, jsonb_array_elements(j) j
                                  from (
                                      select id_etab, jsonb_agg(etab) as j
                                      from (
                                          select * from etablissement_annee ea
                                          WHERE (ea.origin_mat = :origineMat or :origineMat is null)
                                      ) etab
                                      group by id_etab
                                  ) etab_json
                              ) ej
                              cross join lateral jsonb_each(ej.j)
                              left outer join  {h-schema}metadonnee on nom_table = 'etablissement' and nom_champ = key
                          ) fe
                          group by id_etab
                      ) joa
                      cross join lateral jsonb_populate_record(cast(null as {h-schema}etablissement), joa.ag) etab_filtre
                  ),
                  destination_filtre_json as (
                      select id_etablissement as id_etab, jsonb_strip_nulls(jsonb_object_agg(fd.key, fd.value)) dag
                      from (
                          select id_destination, id_etablissement, key,
                          case if_public
                              when :ifPublic then value
                              when true then value
                          end as value
                          from (
                              SELECT id_destination, id_etablissement, jsonb_array_elements(j) j
                              from (
                                  select id_destination, id_etablissement, jsonb_agg(dest) as j
                                  from (
                                      select id_destination, id_etablissement, annee, famille_rattachement_destination, type_produit_destination,  replace(concat(libelle_destination, ' (', insee_departement, ')'), ' ()', '') as libelle_destination, tonnage_destination, to_char(date_maj, 'DD/MM/YYYY HH24:MI:SS:MS') as date_maj
                                      from {h-schema}destination
                                      left join {h-schema}ref_departement ON libelle_destination = nom_departement
                                  ) dest
                                  where dest.annee = CAST(:annee AS INTEGER)
                                  group by id_destination, id_etablissement
                              ) dest_json
                          ) ej
                          cross join lateral jsonb_each(ej.j)
                          left outer join  {h-schema}metadonnee on nom_table = 'destination' and nom_champ = key
                      ) fd
                      group by id_destination, id_etablissement
                  ),
                  dechets_filtre_json as (
                      select id_etablissement as id_etab, jsonb_strip_nulls(jsonb_object_agg(fd.key, fd.value)) tag
                      from (
                          select id_traitement_dechet, id_etablissement, key,
                          case if_public
                              when :ifPublic then value
                              when true then value
                          end as value
                          from (
                              SELECT id_traitement_dechet, id_etablissement, jsonb_array_elements(j) j
                              from (
                                  select id_traitement_dechet, id_etablissement, jsonb_agg(trait) as j
                                  from (
                                      select id_traitement_dechet, id_etablissement, annee, code_dechet, libelle_dechet, if_statut_sortie_dechet, departement_origine, pays_origine, quantite_admise, quantite_traitee, code_ope, libelle_ope, numero_notification, to_char(date_maj, 'DD/MM/YYYY HH24:MI:SS:MS') as date_maj
                                      from {h-schema}traitement_dechet
                                  ) trait
                                  where trait.annee = CAST(:annee AS INTEGER)
                                  group by id_traitement_dechet, id_etablissement
                              ) trait_json
                          ) ej
                          cross join lateral jsonb_each(ej.j)
                          left outer join  {h-schema}metadonnee on nom_table = 'traitement_dechet' and nom_champ = key
                      ) fd
                      group by id_traitement_dechet, id_etablissement
                  ),
                  contraintes_env_etab_etude as (
                      select ea.id_etab id_etab_tech, jsonb_agg(id_contr_env) contrainte_environnementale
                      from etablissement_annee ea
                      join {h-schema}contrainte_environnementale ce on ce.id_etude = :idEtude
                      where :idEtude is not null
                      and exists(select 1 from {h-schema}scenario where id_etude = :idEtude)
                      and public.st_intersects(ce.the_geom, ea.the_geom)
                      group by ea.id_etab
                  )
                  select jsonb_strip_nulls(to_jsonb(res)) - 'id_tech' as payload
                  from (
                      select etablissement_filtre.*, traitement_dechet.traitement_dechet, destinations.destinations, cee.contrainte_environnementale
                      from etablissement_filtre
                      left join (
                          select t_dech.id_etab, jsonb_agg(t_dech.tag) as traitement_dechet
                          from dechets_filtre_json as t_dech
                          where not t_dech.tag <@ '{}'
                          group by t_dech.id_etab
                      ) as traitement_dechet on traitement_dechet.id_etab = etablissement_filtre.id_tech
                      left join (
                          select t_dest.id_etab, jsonb_agg(t_dest.dag) as destinations
                          from destination_filtre_json as t_dest
                          where not t_dest.dag <@ '{}'
                          group by t_dest.id_etab
                      ) destinations on destinations.id_etab = etablissement_filtre.id_tech
                      left join contraintes_env_etab_etude cee on cee.id_etab_tech = etablissement_filtre.id_tech
                  ) res
                  where :idEtude is null
                  or res.id_tech in (
                      select ea.id_etab
                      from etablissement_annee  ea
                      join {h-schema}destination d on ea.id_etab = d.id_etablissement
                      join {h-schema}v_departement_intersect_etude ide on ide.nom_departement = d.libelle_destination
                      and ide.id_etude = :idEtude
                      and d.annee = CAST(:annee AS INTEGER)
                      and (:territoireSeul is null or not :territoireSeul)
                    UNION
                      SELECT ea.id_etab
                      from {h-schema}territoire t
                      join etablissement_annee ea on public.ST_INTERSECTS(t.the_geom, ea.the_geom)
                      where t.id_etude = :idEtude
                  )
                  """, nativeQuery = true)
  List<Map<String, Object>> findEtablissementAnneeEtOrigineMatEtEtude(@NonNull @Param("annee") String annee, @Nullable @Param("origineMat") String origineMat, @NonNull @Param("ifPublic") boolean ifPublic, @Nullable @Param("idEtude") Long idEtude, @Nullable @Param("territoireSeul") Boolean territoireSeul);



}

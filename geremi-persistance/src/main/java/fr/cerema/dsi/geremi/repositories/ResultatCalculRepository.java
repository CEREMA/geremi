package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.dto.BesoinTotalChantierAnneeZoneDTO;
import fr.cerema.dsi.geremi.dto.ProductionZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResultatCalculRepository extends GenericRepository<ResultatCalcul,Long> {

  @Query(value = "SELECT * FROM resultat_calcul WHERE id_scenario =:idScenario",nativeQuery = true)
  List<ResultatCalcul> findByIdScenario(@Param("idScenario") Long idScenario);

  @Query(value = """
                SELECT rc.*
                FROM resultat_calcul rc
                JOIN scenario s on s.id_scenario = rc.id_scenario
                JOIN etude e on s.id_etude = e.id_etude
                WHERE rc.id_scenario =:idScenario
                AND rc.if_projection = false
                AND rc.annee between :anneeDebut and :anneeFin
                """,nativeQuery = true)
  List<ResultatCalcul> findByIdScenarioReelPeriode(@Param("idScenario") Long idScenario,@Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin);

  /**
   * Liste les ResultatCalcul existants pour un scenario pour l'UC de calcul (édition scénario)
   * Les résultats retournés sont :
   *  - le réel pour l'année ref
   *  - les projections pour les années suivantes
   *
   * @param idScenario
   * @return les resultats calcul du scenario trouvés
   */
  @Query(value = """
                SELECT rc.*
                FROM {h-schema}resultat_calcul rc
                JOIN {h-schema}scenario s on s.id_scenario = rc.id_scenario
                JOIN {h-schema}etude e on s.id_etude = e.id_etude
                WHERE rc.id_scenario =:idScenario
                AND (rc.if_projection = true
                    OR (rc.if_projection = false and rc.annee = e.annee_ref)
                )
                """, nativeQuery = true)
  List<ResultatCalcul> findByIdScenarioForCalcul(@Param("idScenario") Long idScenario);
  @Query(value = "SELECT * FROM {h-schema}resultat_calcul WHERE id_scenario =:idScenario and annee = :annee and if_Projection = :ifProjection",nativeQuery = true)
  ResultatCalcul findByIdScenarioAndAnneeAndIfProjection(@Param("idScenario") Long idScenario, @Param("annee") Integer annee, @Param("ifProjection") boolean ifProjection);


  @Query(value = """
                  with
                  chantiers as (
                      select distinct chantier.id_chantier , public.st_transform(chantier.the_geom,srid) as the_geom_srid,chantier.annee_debut,chantier.annee_fin,chantier.beton_pref,chantier.viab_autre,chantier.ton_tot
                      from {h-schema}scenario sce
                      JOIN {h-schema}rel_scenario_chantier relsc ON relsc.id_scenario = sce.id_scenario
                      JOIN {h-schema}chantier_envergure chantier ON chantier.id_chantier = relsc.id_chantier
                      join {h-schema}ref_departement_geometry dg on public.ST_INTERSECTS(chantier.the_geom, dg.the_geom) = true
                      where sce.id_scenario = :idScenario
                  ),zones as (
                      select distinct zone.id_zone, public.st_transform(zone.the_geom,srid) as the_geom_srid
                      FROM {h-schema}scenario sce
                      JOIN {h-schema}etude etude ON sce.id_etude = etude.id_etude
                      JOIN {h-schema}zone zone ON zone.id_etude = etude.id_etude
                      join {h-schema}ref_departement_geometry dg on public.ST_INTERSECTS(zone.the_geom, dg.the_geom) = true
                      where sce.id_scenario = :idScenario
                  ),
                  besoinsChantiers as (
                      select annee, zones.id_zone, chantiers.id_chantier,
                             case
                                 when public.GeometryType(chantiers.the_geom_srid) = 'MULTIPOLYGON' and public.st_area(chantiers.the_geom_srid) > 0 then public.st_area(public.st_intersection(chantiers.the_geom_srid, zones.the_geom_srid)) / public.st_area(chantiers.the_geom_srid)
                                 when public.GeometryType(chantiers.the_geom_srid) = 'MULTILINESTRING' and public.st_length(chantiers.the_geom_srid) > 0 then public.st_length(public.st_intersection(chantiers.the_geom_srid, zones.the_geom_srid)) / public.st_length(chantiers.the_geom_srid)
                                 when public.GeometryType(chantiers.the_geom_srid) = 'POINT' then 1
                                 else 0
                             end as ratio,
                             case
                                 when ton_tot > 0 then ton_tot
                                 else coalesce(beton_pref,0) + coalesce(viab_autre,0)
                             end as BesoinChantierZone ,
                              chantiers.annee_fin - chantiers.annee_debut + 1 as nb_annee
                      FROM {h-schema}scenario sce
                      JOIN {h-schema}etude etude ON sce.id_etude = etude.id_etude
                      cross join chantiers
                      join zones on public.ST_INTERSECTS(chantiers.the_geom_srid, zones.the_geom_srid)
                      join generate_series(etude.annee_ref, etude.annee_fin) annee on chantiers.annee_debut <= annee and chantiers.annee_fin >= annee
                      where sce.id_scenario = :idScenario
                  )
                  select annee, id_zone as idZone, sum(BesoinChantierZone * ratio / nb_annee) as besoinTotalChantier
                  from besoinsChantiers
                  group by annee, id_zone
                  """, nativeQuery = true)
  List<BesoinTotalChantierAnneeZoneDTO> besoinTotalChantierAnneeZone(@Param("idScenario") Long idScenario);


  @Query(value = """
                  with
                      intersection_zone_departement_etude as (
                          select z.id_etude, z.id_zone, departement_intersect_etude.id_departement, departement_intersect_etude.nom_departement,departement_intersect_etude.SRID,
                                 public.st_area(public.ST_Transform(z.the_geom,departement_intersect_etude.SRID)) / 1000000 surfaceZone,
                                 public.st_area(public.st_intersection(public.ST_Transform(departement_intersect_etude.dept_geom,departement_intersect_etude.SRID), public.ST_Transform(departement_intersect_etude.territoire_geom,departement_intersect_etude.SRID))) / 1000000 SurfaceDept,
                                 public.st_area(public.st_intersection(public.ST_Transform(departement_intersect_etude.dept_geom,SRID), public.ST_Transform(z.the_geom,SRID))) / 1000000 as surfaceIntersectZoneDept
                          from {h-schema}zone z
                                   join {h-schema}etude e on e.id_etude = z.id_etude
                                   join  {h-schema}scenario s on e.id_etude = s.id_etude
                                   join (SELECT t.id_etude,
                                                t.the_geom as territoire_geom,
                                                dept_geom.id                                  as id_departement,
                                                trim(BOTH ' ' FROM dept_geom.nom_departement) as nom_departement,
                                                dept_geom.the_geom                            as dept_geom,
                                                dept_geom.SRID
                                         from {h-schema}territoire t
                                                  join (
                                             select d.id, d.nom_departement, dg.the_geom, dg.SRID
                                             from {h-schema}ref_departement d
                                                      join {h-schema}ref_departement_geometry dg on d.id = dg.id
                                         ) as dept_geom  on public.st_area(public.ST_REDUCEPRECISION(public.st_intersection(t.the_geom, dept_geom.the_geom),0.000001)) > 0
                          ) departement_intersect_etude
                                        on e.id_etude = departement_intersect_etude.id_etude
                                            and public.st_area(public.ST_REDUCEPRECISION(public.st_intersection(departement_intersect_etude.dept_geom, z.the_geom),0.000001)) > 0
                          where s.id_scenario = :idScenario
                      ),
                      etablissement_renouvellement as (
                          select d.annee as annee_dest, et.id_etab, et.the_geom, et.date_fin_autorisation, dept_etude.id_zone, dept_etude.id_departement, d.libelle_destination, d.tonnage_destination , d.type_produit_destination,
                                 case
                                     when substr(d.type_produit_destination, 1,5) in ('C1.01','C1.03','C1.04') then 'Beton'
                                     when substr(d.type_produit_destination, 1,5) in ('C1.02','C1.05','C1.06','C1.07','C1.08', 'C2.99') then 'Viab'
                                     end as tpd,
                                 min(tx_renouvellement_contrainte) as min_taux_renouvellement_contraintes
                          from {h-schema}etablissement et
                                   join {h-schema}scenario s on s.id_scenario = :idScenario
                                   join {h-schema}etude e on e.id_etude = s.id_etude
                                   join {h-schema}destination d on et.id_etab = d.id_etablissement and d.annee in (select an from generate_series(:anneeRelleDebut, :anneeRelleFin) an)
                                   left outer join (
                              select ce.the_geom, rsc.tx_renouvellement_contrainte
                              FROM {h-schema}contrainte_environnementale ce
                                       JOIN {h-schema}rel_scenario_contrainte rsc ON ce.id_contr_env = rsc.id_contrainte
                              where rsc.id_scenario = :idScenario
                          ) contrainte_renouvellement on public.st_intersects(et.the_geom, contrainte_renouvellement.the_geom)
                                   join intersection_zone_departement_etude dept_etude on d.libelle_destination = dept_etude.nom_departement and dept_etude.id_etude = e.id_etude
                                   cross join generate_series(:anneeDebut, :anneeFin) an
                          group by d.id_destination, d.annee, et.id_etab, et.the_geom, et.date_fin_autorisation, dept_etude.id_zone, dept_etude.id_departement, d.libelle_destination, d.tonnage_destination, tpd, d.type_produit_destination
                      ),
                      rsd as (
                          select id_departement, repartition_departement_beton as repartitionDepartement, 'Beton' as tpd
                          from {h-schema}rel_scenario_departement where id_scenario = :idScenario
                          UNION
                          select id_departement, repartition_departement_viabilite as repartitionDepartement, 'Viab' as tpd
                          from {h-schema}rel_scenario_departement where id_scenario = :idScenario
                      ),
                      prod_departement as (
                          select er.annee_dest, an, er.id_etab, er.libelle_destination, er.id_zone, er.id_departement,
                                 case when an <= :anneeRelleFin then 100
                                      when er.date_fin_autorisation >= make_date( an, 1, 1 ) then 100
                                      when er.min_taux_renouvellement_contraintes is null then s.tx_renouvellement_hc
                                      else er.min_taux_renouvellement_contraintes
                                     end as taux_renouvellement,
                                 er.tonnage_destination, er.tpd, type_produit_destination, coalesce(rsd.repartitionDepartement,100) as repartitionDepartement
                          from etablissement_renouvellement er
                                   join {h-schema}scenario s on s.id_scenario = :idScenario
                                   join {h-schema}etude e on s.id_etude = e.id_etude
                                   cross join generate_series(:anneeDebut, :anneeFin) an
                                   left join rsd on rsd.id_departement=er.id_departement and er.tpd=rsd.tpd
                          where er.tpd is not null
                      ),
                      production_departement as (
                          select an, annee_dest, libelle_destination, id_zone,id_departement, tpd, sum(tonnage_destination * taux_renouvellement * repartitionDepartement / 10000) as prod_dept
                          from prod_departement
                          group by an, annee_dest, libelle_destination, id_zone, id_departement, tpd
                          having annee_dest = least(an, :anneeRelleFin)
                      ),
                      pop_annee as (select an, id_zone,
                                           case
                                               when s.dynamique_demographique = 'Basse' then p.population_basse
                                               when s.dynamique_demographique = 'Centrale' then p.population_centrale
                                               when s.dynamique_demographique = 'Haute' then p.population_haute
                                               end as pop
                                    from {h-schema}scenario s
                                             join {h-schema}population p on p.id_etude = s.id_etude
                                             join generate_series(:anneeDebut, :anneeFin) an on p.annee = an
                                    where s.id_scenario = :idScenario
                      ),
                      population_intersec_zone_departement as (
                          select an, izde.id_departement , izde.id_zone, pa.pop * surfaceIntersectZoneDept / surfaceZone as pizd
                          from pop_annee pa
                                   join intersection_zone_departement_etude izde
                                        on pa.id_zone = izde.id_zone
                      ),
                      population_departement_etude as (
                          select an, id_departement ,
                                 sum(pizd) as pop_dept_etude
                          from population_intersec_zone_departement
                          group by an, id_departement
                      ),
                      pourcentage_population_intersec_zone_departement as (
                          select pizd.an, pizd.id_zone , pde.id_departement, pizd.pizd / pop_dept_etude as ratio
                          from population_intersec_zone_departement pizd
                                   join population_departement_etude pde on pde.id_departement = pizd.id_departement and pde.an = pizd.an
                      )
                  select pdpt.an as annee, annee_dest, izde.id_zone as idZone, pdpt.tpd as typeProduction,sum(pdpt.prod_dept * PRS * surfaceIntersectZoneDept / SurfaceDept + pdpt.prod_dept * PRP * ppizd.ratio) as productionZone
                  from intersection_zone_departement_etude izde
                           left join production_departement pdpt on izde.id_zone = pdpt.id_zone and izde.id_departement = pdpt.id_departement
                           left join pourcentage_population_intersec_zone_departement ppizd on izde.id_zone = ppizd.id_zone and izde.id_departement = ppizd.id_departement and pdpt.an = ppizd.an
                           join (
                      select cast(ponderation_surface_beton as numeric) /100 as PRS, cast(100 -  ponderation_surface_beton as numeric) /100 as PRP, 'Beton' as tpd
                      from {h-schema}scenario where id_scenario = :idScenario
                      UNION
                      select cast(ponderation_surface_viabilite as numeric) /100 as PRS,  cast(100 - ponderation_surface_viabilite as numeric) /100 as PRP, 'Viab' as tpd
                      from {h-schema}scenario where id_scenario = :idScenario
                  ) pr on pr.tpd = pdpt.tpd
                  group by  pdpt.an, annee_dest, izde.id_zone, pdpt.tpd
                  """, nativeQuery = true)
  List<ProductionZone> productionsPrimairesAnneeZonePeriodes(@Param("idScenario") Long idScenario, @Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin, @Param("anneeRelleDebut") Integer anneeRelleDebut, @Param("anneeRelleFin") Integer anneeRelleFin);



  @Query(value = """
                  with
                  intersection_zone_departement_etude as (
                      select z.id_etude, z.id_zone, departement_intersect_etude.id_departement, departement_intersect_etude.nom_departement,departement_intersect_etude.SRID,
                             public.st_area(public.ST_Transform(z.the_geom,departement_intersect_etude.SRID)) / 1000000 surfaceZone,
                             public.st_area(public.st_intersection(public.ST_Transform(departement_intersect_etude.dept_geom,departement_intersect_etude.SRID), public.ST_Transform(departement_intersect_etude.territoire_geom,departement_intersect_etude.SRID))) / 1000000 SurfaceDept,
                             public.st_area(public.st_intersection(public.ST_Transform(departement_intersect_etude.dept_geom,SRID), public.ST_Transform(z.the_geom,SRID))) / 1000000 as surfaceIntersectZoneDept
                      from {h-schema}zone z
                       join {h-schema}etude e on e.id_etude = z.id_etude
                       join {h-schema}scenario s on e.id_etude = s.id_etude
                       join (SELECT t.id_etude,
                                    t.the_geom as territoire_geom,
                                    dept_geom.id                                  as id_departement,
                                    trim(BOTH ' ' FROM dept_geom.nom_departement) as nom_departement,
                                    dept_geom.the_geom                            as dept_geom,
                                    dept_geom.SRID
                             from {h-schema}territoire t
                              join (
                                 select d.id, d.nom_departement, dg.the_geom, dg.SRID
                                 from {h-schema}ref_departement d
                                          join {h-schema}ref_departement_geometry dg on d.id = dg.id
                              ) as dept_geom  on public.st_area(public.ST_REDUCEPRECISION(public.st_intersection(t.the_geom, dept_geom.the_geom),0.000001)) > 0
                      ) departement_intersect_etude
                                    on e.id_etude = departement_intersect_etude.id_etude
                                    and public.st_area(public.ST_REDUCEPRECISION(public.st_intersection(departement_intersect_etude.dept_geom, z.the_geom),0.000001)) > 0
                      where s.id_scenario = :idScenario
                  ),
                  etablissement_renouvellement as (
                      select et.id_etab, et.the_geom, et.date_fin_autorisation, z.id_zone, dept_etude.id_departement, d.libelle_destination, d.tonnage_destination , d.type_produit_destination,
                             case
                                 when substr(d.type_produit_destination, 1,5) in ('C1.01','C1.03','C1.04') then 'Beton'
                                 when substr(d.type_produit_destination, 1,5) in ('C1.02','C1.05','C1.06','C1.07','C1.08', 'C2.99') then 'Viab'
                             end as tpd,
                             min(tx_renouvellement_contrainte) as min_taux_renouvellement_contraintes
                      from {h-schema}etablissement et
                           join {h-schema}scenario s on s.id_scenario = :idScenario
                           join {h-schema}etude e on e.id_etude = s.id_etude
                           join {h-schema}zone z on public.st_intersects(et.the_geom, z.the_geom) and z.id_etude = e.id_etude
                           join {h-schema}destination d on et.id_etab = d.id_etablissement and d.annee = e.annee_ref
                           left outer join (
                              select ce.the_geom, rsc.tx_renouvellement_contrainte
                              FROM {h-schema}contrainte_environnementale ce
                                       JOIN {h-schema}rel_scenario_contrainte rsc ON ce.id_contr_env = rsc.id_contrainte
                              where rsc.id_scenario = :idScenario
                          ) contrainte_renouvellement on public.st_intersects(et.the_geom, contrainte_renouvellement.the_geom)
                          join intersection_zone_departement_etude dept_etude on d.libelle_destination = dept_etude.nom_departement and dept_etude.id_etude = e.id_etude and z.id_zone = dept_etude.id_zone
                      group by d.id_destination, et.id_etab, et.the_geom, et.date_fin_autorisation, z.id_zone, dept_etude.id_departement, d.libelle_destination, d.tonnage_destination, tpd, d.type_produit_destination
                  ),
                  rsd as (
                      select id_departement, repartition_departement_beton as repartitionDepartement, 'Beton' as tpd
                      from {h-schema}rel_scenario_departement where id_scenario = :idScenario
                      UNION
                      select id_departement, repartition_departement_viabilite as repartitionDepartement, 'Viab' as tpd
                      from {h-schema}rel_scenario_departement where id_scenario = :idScenario
                  ),
                  prod_interne as (
                      select an, er.id_zone, er.id_etab, er.libelle_destination, er.id_departement,
                             case when er.date_fin_autorisation >= make_date( an, 1, 1 ) then 100
                                  when er.min_taux_renouvellement_contraintes is null then s.tx_renouvellement_hc
                                  else er.min_taux_renouvellement_contraintes
                             end as taux_renouvellement,
                             er.tonnage_destination, er.tpd, type_produit_destination, coalesce(rsd.repartitionDepartement,100) as repartitionDepartement
                      from etablissement_renouvellement er
                      join {h-schema}scenario s on s.id_scenario = :idScenario
                      join {h-schema}etude e on s.id_etude = e.id_etude
                      cross join generate_series(e.annee_ref, e.annee_fin) an
                      left join rsd on rsd.id_departement=er.id_departement and er.tpd=rsd.tpd
                      where er.tpd is not null
                  ),
                  pop_annee as (select an, id_zone,
                                       case
                                           when s.dynamique_demographique = 'Basse' then p.population_basse
                                           when s.dynamique_demographique = 'Centrale' then p.population_centrale
                                           when s.dynamique_demographique = 'Haute' then p.population_haute
                                           end as pop
                                from {h-schema}scenario s
                                 join {h-schema}etude e on s.id_etude = e.id_etude
                                 join {h-schema}population p on p.id_etude = s.id_etude
                                 join generate_series(e.annee_ref, e.annee_fin) an on p.annee = an
                                where s.id_scenario = :idScenario
                  ),
                  population_intersec_zone_departement as (
                      select an, izde.id_departement , izde.id_zone, pa.pop * surfaceIntersectZoneDept / surfaceZone as pizd
                      from pop_annee pa
                               join intersection_zone_departement_etude izde
                                    on pa.id_zone = izde.id_zone
                  ),
                  population_departement_etude as (
                      select an, id_departement ,
                             sum(pizd) as pop_dept_etude
                      from population_intersec_zone_departement
                      group by an, id_departement
                  ),
                  pourcentage_population_intersec_zone_departement as (
                      select pizd.an, pizd.id_zone , pde.id_departement, pizd.pizd / pop_dept_etude as ratio
                      from population_intersec_zone_departement pizd
                               join population_departement_etude pde on pde.id_departement = pizd.id_departement and pde.an = pizd.an
                  ),
                  production_interne_departement as (
                      select an, libelle_destination, id_departement, tpd, sum(tonnage_destination * taux_renouvellement * repartitionDepartement / 10000) as prod_interne
                      from prod_interne
                      group by an, libelle_destination, id_departement, tpd
                  )
                  select pid.an as annee, izde.id_zone as idZone, pid.tpd as typeProduction, sum(pid.prod_interne * PRS * surfaceIntersectZoneDept / SurfaceDept + pid.prod_interne * PRP * ppizd.ratio) as productionZone
                  from intersection_zone_departement_etude izde
                  left join production_interne_departement pid on izde.id_departement = pid.id_departement
                  left join pourcentage_population_intersec_zone_departement ppizd on izde.id_zone = ppizd.id_zone and izde.id_departement = ppizd.id_departement and pid.an = ppizd.an
                  join (
                      select cast(ponderation_surface_beton as numeric) /100 as PRS, (100 - cast(ponderation_surface_beton as numeric)) /100 as PRP, 'Beton' as tpd
                      from {h-schema}scenario where id_scenario = :idScenario
                      UNION
                      select cast(ponderation_surface_viabilite as numeric) /100 as PRS, (100 - cast(ponderation_surface_viabilite as numeric)) /100 as PRP, 'Viab' as tpd
                      from {h-schema}scenario where id_scenario = :idScenario
                  ) pr on pr.tpd = pid.tpd
                  group by  pid.an, izde.id_zone, pid.tpd
                  """
    , nativeQuery = true)
  List<ProductionZone> getProductionsInterneZone(@Param("idScenario") Long idScenario);

  @Query(value = """
                  with
                      etablissement_renouvellement as (
                          select d.annee as annee_dest, et.id_etab, et.the_geom, et.date_fin_autorisation, z.id_zone, d.tonnage_destination , d.type_produit_destination,
                                 min(tx_renouvellement_contrainte) as min_taux_renouvellement_contraintes
                          from {h-schema}etablissement et
                                                 join {h-schema}scenario s on s.id_scenario = :idScenario
                              join {h-schema}etude e on e.id_etude = s.id_etude
                              join {h-schema}zone z on public.st_intersects(et.the_geom, z.the_geom) and z.id_etude = e.id_etude
                              join  {h-schema}destination d on et.id_etab = d.id_etablissement
                                                         and d.annee in (select an from generate_series(:anneeRelleDebut, :anneeRelleFin) an)
                                                         and substr(d.type_produit_destination, 1,5) in ('C1.01','C1.03','C1.04','C1.02','C1.05','C1.06','C1.07','C1.08','C2.99')
                              left outer join (
                              select ce.the_geom, rsc.tx_renouvellement_contrainte
                              FROM {h-schema}contrainte_environnementale ce
                              JOIN {h-schema}rel_scenario_contrainte rsc ON ce.id_contr_env = rsc.id_contrainte
                              where rsc.id_scenario = :idScenario
                              ) contrainte_renouvellement on public.st_intersects(et.the_geom, contrainte_renouvellement.the_geom)

                          group by d.id_destination, d.annee, et.id_etab, et.the_geom, et.date_fin_autorisation, z.id_zone, d.tonnage_destination, d.type_produit_destination
                      ),
                      prod_brute as (
                          select er.annee_dest, an, er.id_zone, er.id_etab,
                                 case when an <= :anneeRelleFin then 100
                                      when er.date_fin_autorisation >= make_date( an, 1, 1 ) then 100
                                      when er.min_taux_renouvellement_contraintes is null then s.tx_renouvellement_hc
                                      else er.min_taux_renouvellement_contraintes
                                     end as taux_renouvellement,
                                 er.tonnage_destination, type_produit_destination
                          from etablissement_renouvellement er
                                   join {h-schema}scenario s on s.id_scenario = :idScenario
                              join {h-schema}etude e on s.id_etude = e.id_etude
                              cross join generate_series(:anneeDebut, :anneeFin) an
                      )
                  select an as annee, id_zone as idZone, sum(tonnage_destination * taux_renouvellement / 100) as productionZone
                  from prod_brute
                  group by an, annee_dest, id_zone
                  having annee_dest = least(an, :anneeRelleFin)
                  """
    , nativeQuery = true)
  List<ProductionZone> getProductionsZonePrimaireBrute(@Param("idScenario") Long idScenario, @Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin, @Param("anneeRelleDebut") Integer anneeRelleDebut, @Param("anneeRelleFin") Integer anneeRelleFin);


  @Query(value = """
      select min(an), max(an)
      from etude e
      join scenario s on e.id_etude = s.id_etude
      cross join generate_series(e.annee_ref, e.annee_fin) an
      join (
        select distinct annee from {h-schema}declaration
      ) annee_declaration on annee_declaration.annee = an
      where id_scenario = :idScenario
      and not exists(
        select 1
        from resultat_calcul rc
        where rc.id_scenario = s.id_scenario
        and rc.annee = an
        and rc.if_projection = false
      )
      """
    , nativeQuery = true)
  Tuple getAnneesSuiviNonCalculees(@Param("idScenario") Long idScenario);
}

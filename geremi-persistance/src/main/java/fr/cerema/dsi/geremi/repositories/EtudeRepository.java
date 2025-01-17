package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Etude;

public interface EtudeRepository extends GenericRepository<Etude, Long> {

  @Query(value = """
                  select et.* from (
                      select e.* from {h-schema}etude as e
                      where  e.id_users  = :idUtilisateur
                      union
                      select e.* from {h-schema}rel_etude_user_procuration as rel
                      join {h-schema}etude as e on e.id_etude = rel.id_etude  and rel.id_users = :idUtilisateur
                      union
                      select e.* from {h-schema}etude e
                      join users u on u.id_users = :idUtilisateur
                      join profil p on u.id_profil = p.id_profil
                      where p.libelle in ('ADMIN', 'GEST')
                  ) et
                  join (
                      select id_etude, max(date_maj) as date_derniere_modif
                      from {h-schema}tracabilite_etape group by id_etude
                  ) te on te.id_etude = et.id_etude
                  order by date_derniere_modif desc
                 """, nativeQuery = true)
  List<Etude> findEtudeUtilisateur(@Param("idUtilisateur") Long idUtilisateur);

  @Query(value = """
                  select et.* from (
                      select e.* from {h-schema}etude as e
                      where  e.id_users  = :idUtilisateur
                      union
                      select e.* from {h-schema}rel_etude_user_procuration as rel
                      join {h-schema}etude as e on e.id_etude = rel.id_etude  and rel.id_users = :idUtilisateur
                      union
                      select e.* from {h-schema}etude e
                      join users u on u.id_users = :idUtilisateur
                      join profil p on u.id_profil = p.id_profil
                      where p.libelle in ('ADMIN', 'GEST')
                  ) et

                  inner join (
                      select id_etude
                      from {h-schema}scenario s where s.if_retenu = true
                  ) sc on sc.id_etude = et.id_etude
                  order by id_etude desc
                 """, nativeQuery = true)
  List<Etude> findEtudeSuiviUtilisateur(@Param("idUtilisateur") Long idUtilisateur);

  @Query(value = """
                  select e.* from {h-schema}etude e
                  inner join (
                      select id_etude
                      from {h-schema}scenario s where s.if_retenu = true
                  ) sc on sc.id_etude = e.id_etude
                  where e.if_src = true and e.if_public = true
                  order by e.id_etude desc
                 """, nativeQuery = true)
  List<Etude> findEtudePublic(@Param("idUtilisateur") Long idUtilisateur);
}


package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.Profil;

public interface ProfilRepository extends GenericRepository<Profil, Long> {

  @Query(value = "select p.profilutilisateur from {h-schema}users as u join {h-schema}profil as p on p.id = u.profil where lower(u.email) = lower(:usersname)", nativeQuery = true)
  List<Profil> findAllCurrentRolesByUsername(@Param("usersname") String usersname);

  List<Profil> findByLibelle(String role);

}


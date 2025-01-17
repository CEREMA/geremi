package fr.cerema.dsi.geremi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.User;

public interface UserRepository extends GenericRepository<User, Long> {

	  @Query(value = "select u.id_users, u.nom,  u.prenom, u.mail, u.id_ref_etat, u.id_profil, u.id_ref_region, u.date_creation from {h-schema}users as u where  u.mail  = :email ", nativeQuery = true)
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query(value = "SELECT DISTINCT u FROM User u JOIN u.profil p WHERE p.libelle = :libelleRole")
    List<User> findAllByRole(@Param("libelleRole") String libelleRole);
}


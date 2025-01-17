package fr.cerema.dsi.geremi.services;

import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Profil;

public interface ProfilService extends GenericService<Profil, Long> {

  /**
   * Récupère tous les rôles de la direction courante de l'utilisateur
   *
   * @param usersname l'utilisateur pour lequel récupérer les rôles
   * @return la liste des rôles
   */
  List<Profil> findAllCurrentRolesByUsername(String usersname);

  /**
   * Récupère l'entite profil corrspondant à l'enum Role
   *
   * @param role la valeur de l'enum à rechercher
   * @return le profil correspondant
   */
  List<Profil> findByLibelle(String role);
}

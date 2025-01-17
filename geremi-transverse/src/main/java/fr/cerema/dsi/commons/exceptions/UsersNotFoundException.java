package fr.cerema.dsi.commons.exceptions;

/**
 * Exception à lever lorsque l'utilisateur n'a pas été trouvé
 */
public class UsersNotFoundException extends RuntimeException {

  public UsersNotFoundException(String message) {
    super(message);
  }
}

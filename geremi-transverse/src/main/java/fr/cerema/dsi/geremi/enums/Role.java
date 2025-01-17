package fr.cerema.dsi.geremi.enums;

/**
 * Les rôles possibles de l'application
 */
public enum Role {
  PUBLIC("PUBLIC"),
  DREAL("DREAL"),
  CEREMA("CEREMA"),
  ADMIN("ADMIN"),
  GEST("GEST");

  private final String libelle;

  Role(String libelle) {
    this.libelle = libelle;
  }

  public String getLibelle(){
    return libelle;
  }
}

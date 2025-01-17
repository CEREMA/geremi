package fr.cerema.dsi.geremi.enums;

/**
 * Les rôles possibles de l'application
 */
public enum Etat {
  EN_ATTENTE("En attente validation"),
  VALIDE("Validé"),
  REFUSE("Refusé");

  private final String libelle;

  Etat(String libelle) {
    this.libelle = libelle;
  }

  public String getLibelle(){
    return libelle;
  }
}

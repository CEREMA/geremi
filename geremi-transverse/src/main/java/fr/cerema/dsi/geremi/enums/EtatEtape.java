package fr.cerema.dsi.geremi.enums;

/**
 * Les rôles possibles de l'application
 */
public enum EtatEtape {
  VALIDE("validé"),

  VALIDE_VIDE("validé à vide"),
  IMPORTE("importé"),
  MODIFIE("modifié"),
  NON_RENSEIGNE("non renseigné");

  private final String libelle;


  EtatEtape(String libelle) {
    this.libelle = libelle;
  }

  public String getLibelle(){
    return libelle;
  }


}

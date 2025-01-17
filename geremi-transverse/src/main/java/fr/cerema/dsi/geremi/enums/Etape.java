package fr.cerema.dsi.geremi.enums;

/**
 * Les rôles possibles de l'application
 */
public enum Etape {
  CREATION("création", 1, false),
  ZONAGE("zonage", 2, false),
  POPULATION("population", 3, false),
  CONTRAINTES("contraintes", 4, false),
  CHANTIERS("chantiers", 5, false),
  INSTALLATIONS("installations", 6, false),
  MATERIAUX("matériaux", 7, false),

  CREATION_SCENARIO("création scénario", 8, true),
  CONTRAINTES_SCENARIO("contraintes scénario", 9, true),
  CHANTIERS_SCENARIO("chantiers scénario", 10, true),
  INSTALLATIONS_SCENARIO("installations scénario", 11, true),
  MATERIAUX_SCENARIO("matériaux scénario", 12, true),
  HYPOTHESE_VENTILATION_SCENARIO("ventilation", 13, true),
  HYPOTHESE_PROJECTION_SCENARIO("projection", 14, true);

  private final String libelle;
  private final int index;

  private final boolean isScenario;

  Etape(String libelle, int index, boolean isScenario) {
    this.libelle = libelle;
    this.index = index;
    this.isScenario = isScenario;
  }

  public String getLibelle(){
    return libelle;
  }
  public Integer getIndex(){
    return index;
  }
  public boolean isScenario() { return isScenario; }
}

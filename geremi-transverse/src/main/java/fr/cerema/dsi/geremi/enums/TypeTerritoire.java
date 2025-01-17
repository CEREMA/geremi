package fr.cerema.dsi.geremi.enums;

public enum TypeTerritoire {
  REGION("region"),
  DEPARTEMENT("departement"),
  ZONE_EMPLOI("zoneemploi"),
  BASSIN_VIE("bassinvie"),
  EPCI("epci"),
  COMMUNE("commune");

  private String type;

  TypeTerritoire(String type){
    this.type = type;
  }

  public String type(){
    return this.type;
  }
}

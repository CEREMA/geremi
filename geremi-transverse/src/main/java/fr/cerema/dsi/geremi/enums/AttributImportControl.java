package fr.cerema.dsi.geremi.enums;

public enum AttributImportControl {
  
  CPG("cpg"),
  DBF ("dbf"),
  PRJ ("prj"),
  SHP ("shp"),
  SHX ("shx"),
  GPKG("gpkg"),
  UTF8("UTF-8"),
  GCS_WGS_1984("GCS_WGS_1984"),
  WGS_84("WGS 84"),
  EPSG_WGS_84("EPSG:WGS 84"),
  NOM("nom"),
  DESCRIP("descrip"),
  NIVEAU("niveau"),
  THEGEOM("the_geom"),
  GEOM("geom"),
  CODE_ZONE("code_zone"),
  NOM_ZONE("nom_zone"),
  NOM_ETAB("nom_etab"),
  CODE_ETAB("code_etab"),
  ANNEE_DEBUT("an_deb"),
  ANNEE_FIN("an_fin"),
  BETON_PREF("beton_pref"),
  VIAB_AUTRE("viab_autre"),
  TON_TOT("ton_tot"),
  FORTE("Forte"),
  MOYENNE("Moyenne"),
  FAIBLE("Faible"),
  PRODUCTIONZONETOTAL("prodtot"), 
  PRODUCTIONZONEPRIMAIRETOTAL("prodprim"),
  BESOINZONETOTAL("bestot"),
  ANNEE("annee");
  
	private final String libelle;

	AttributImportControl(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}

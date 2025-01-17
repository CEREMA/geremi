package fr.cerema.dsi.commons.services;

public interface CalculsService {
  String[] BETON_TYPES = new String[] { "C1.01%", "C1.03%", "C1.04%" };
   String[] VIAB_TYPES = new String[] { "C1.02%", "C1.05%", "C1.06%", "C1.07%", "C1.08%", "C2.99%" };

  String PRIMAIRE = "Primaire";
  String NATUREL = "Naturel";
  String SECONDAIRE = "Secondaire";
  String RECYCLE = "Recyclé";
  String ARTIFICIEL = "Artificiel";

}

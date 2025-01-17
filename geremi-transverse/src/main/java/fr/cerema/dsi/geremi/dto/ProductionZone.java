package fr.cerema.dsi.geremi.dto;

import java.math.BigDecimal;

public interface ProductionZone {

  int getAnnee();
  Long getIdZone();
  String getTypeProduction() ;
  BigDecimal getProductionZone();

}

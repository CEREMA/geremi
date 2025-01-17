package fr.cerema.dsi.geremi.dto;


import java.io.Serializable;

public interface SelectionZonage {

  Long getId();
  String getCode();
  String getNom() ;
  Boolean getExterieur();
  Serializable getGeomInt();
  Serializable getGeomExt();

}

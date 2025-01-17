package fr.cerema.dsi.geremi.services.dto;


import org.locationtech.jts.geom.Geometry;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "geometry")
public class BassinVieDTO {

  private Long id;
  private String code;
  private String nom;
  private Geometry geometry;
  private Boolean exterieur = false;
}

package fr.cerema.dsi.geremi.services.dto;

import org.locationtech.jts.geom.Geometry;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "geometry")
public class EpciDTO {

  private Long id;
  private String sirenEpci;
  private String nomEpci;
  private String natureEpci;
  private Geometry geometry;
  private Boolean exterieur = false;
}

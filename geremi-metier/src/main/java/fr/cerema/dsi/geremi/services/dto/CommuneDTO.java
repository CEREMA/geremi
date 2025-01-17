package fr.cerema.dsi.geremi.services.dto;


import org.locationtech.jts.geom.Geometry;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "geometry")
public class CommuneDTO {

  private Long id;
  private String inseeCommune;
  private String nomCommune;
  private Geometry geometry;

}

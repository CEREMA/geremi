package fr.cerema.dsi.geremi.services.dto;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "geometry")
@Getter
@Setter
public class ZoneDTO {

	@JsonProperty("id_zone")
	private Long idZone;

	@JsonProperty("type")
	private String type;

	@JsonProperty("nom")
	private String nom;

  @JsonProperty("code")
  private String code;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id_territoire")
	private Long idTerritoire;

  private Geometry geometry;

	@JsonProperty("id_etude")
	private Long idEtude;

  private Boolean exterieur = false;
}

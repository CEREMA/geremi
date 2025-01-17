package fr.cerema.dsi.geremi.services.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TerritoireDto {

	@JsonProperty("id_territoire")
	private Long idTerritoire;

	@JsonProperty("type")
	private String type;

	@JsonProperty("nom")
	private String nom;

	@JsonProperty("description")
	private String description;

	@JsonProperty("liste_territoire")
	private String listeTerritoire;

	@JsonProperty("liste_id")
	private List<Long> listeId;

	@JsonProperty("type_zonage")
	private String typeZonage;

	@JsonProperty("id_etude")
	private Long idEtude;

  @JsonProperty("zone")
  private ZoneDTO zoneDTO;

  @JsonProperty("valideEtape")
  private boolean valideEtape;
}

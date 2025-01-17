package fr.cerema.dsi.geremi.services.dto;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.wololo.geojson.FeatureCollection;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtudeDTO extends GenericDTO {

	/**
	 *
	 */
	@Serial
    private static final long serialVersionUID = 5196754144706017672L;

	@JsonProperty("id")
	private Long id;

	@JsonProperty("nom")
	private String nom;

	@JsonProperty("description")
	private String description;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonProperty("dateCreation")
	private LocalDate dateCreation;

	@JsonProperty("ifSrc")
	private boolean ifSrc;

	@JsonProperty("anneeRef")
	private Integer anneeRef;

	@JsonProperty("anneeFin")
	private Integer anneeFin;

	@JsonProperty("ifPublic")
	private boolean ifPublic;

  @JsonProperty("ifImporte")
  private boolean ifImporte;

  @JsonProperty("readOnly")
  private boolean readOnly;

  @JsonProperty("proprietaire")
  private UserDTO proprietaire;

  @JsonProperty("mandataires")
  private List<UserDTO> mandataires;

  @JsonProperty("territoire")
  private TerritoireDto territoire;

  @JsonProperty("zones")
  private FeatureCollection zones;

  @JsonProperty("populations")
  private List<PopulationDTO> populations;

  @JsonProperty("etatEtapes")
  private Map<Etape, EtatEtape> etatEtapes;

  @JsonProperty("scenarioRetenu")
  private boolean scenarioRetenu;
}

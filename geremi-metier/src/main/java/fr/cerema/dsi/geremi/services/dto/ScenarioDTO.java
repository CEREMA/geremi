package fr.cerema.dsi.geremi.services.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.ToString;

@Data
public class ScenarioDTO {
	@Column(name = "id")
	private Long id;

	@JsonProperty("nom")
	private String nom;

	@JsonProperty("description")
	private String description;

	@JsonProperty("dynamique_demographique")
	private String dynamiqueDemographique;

	@JsonProperty("tx_renouvellement_hc")
	private Integer txRenouvellementHc;

	@JsonProperty("ponderation_surface_beton")
	private Integer ponderationSurfaceBeton;

	@JsonProperty("ponderation_surface_viabilite")
	private Integer ponderationSurfaceViabilite;

	@JsonProperty("if_retenu")
	private boolean ifRetenu;

	@JsonProperty("date_maj")
	private LocalDateTime dateMaj;

	@JsonProperty("etatEtapes")
	private Map<Etape, EtatEtape> etatEtapes;

	@JsonProperty("etudeDTO")
	@ToString.Exclude
	private EtudeDTO etudeDTO;

  @JsonProperty("scenario_contraintes")
  private List<ScenarioContrainteDTO> scenarioContraintes;

	@JsonProperty("scenario_installations")
	private List<ScenarioInstallationDTO> scenarioInstallations;

	@JsonProperty("scenario_chantiers")
	private List<ScenarioChantierDTO> scenarioChantiers;

  @JsonProperty("scenario_materiaux")
  private List<ScenarioMateriauDTO> scenarioMateriaux;

  @JsonProperty("rel_scenario_departement")
  private List<RelScenarioDepartementDTO> relScenarioDepartement;

	@JsonProperty("zone_production_details")
	private List<ZoneProductionDetailsDTO> zoneProductionDetails;


  //map par annee
  @JsonProperty("resultat_calcul")
  Map<Integer, ResultatCalculDTO> resultatsCalculs;

}

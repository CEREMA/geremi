package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ScenarioChantierDTO {
	
  @JsonProperty("id_rel_scenario_chantier")
  private Long idRelScenarioChantier;

  @JsonProperty("id_scenario")
  private Long idScenario;

  @JsonProperty("id_chantier")
  private Long idChantier;
}

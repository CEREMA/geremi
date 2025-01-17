package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScenarioMateriauDTO {
  @JsonProperty("id_rel_scenario_materiau")
  private Long idRelScenarioMateriau;

  @JsonProperty("id_scenario")
  private Long idScenario;

  @JsonProperty("id_zone")
  private Long idZone;

  @JsonProperty("id_materiau")
  private Long idMateriau;

  @JsonProperty("tonnage")
  private Double tonnage;

  @JsonProperty("pourcent")
  private Double pourcent;
}

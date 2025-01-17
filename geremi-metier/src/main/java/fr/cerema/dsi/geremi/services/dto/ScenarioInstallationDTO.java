package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ScenarioInstallationDTO {
  @JsonProperty("id_rel_scenario_installation")
  private Long idRelScenarioInstallation;

  @JsonProperty("id_scenario")
  private Long idScenario;

  @JsonProperty("id_installation")
  private Long idInstallation;
}

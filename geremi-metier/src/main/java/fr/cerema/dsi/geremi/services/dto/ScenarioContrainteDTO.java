package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ScenarioContrainteDTO {
  @JsonProperty("id_rel_scenario_contrainte")
  private Long idRelScenarioContrainte;

  @JsonProperty("id_scenario")
  private Long idScenario;

  @JsonProperty("id_contrainte")
  private Long idContrainte;

  @JsonProperty("tx_renouvellement_contrainte")
  private Integer txRenouvellementContrainte;
}

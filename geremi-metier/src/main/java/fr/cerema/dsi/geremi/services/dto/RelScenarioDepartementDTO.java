package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RelScenarioDepartementDTO {
  @JsonProperty("id_rel_scenario_departement")
  private Long idRelScenarioDepartement;

  @JsonProperty("id_scenario")
  private Long idScenario;

  @JsonProperty("nom")
  private String nom;

  @JsonProperty("id_departement")
  private Long idDepartement;

  @JsonProperty("repartition_departement_beton")
  private Integer repartitionDepartementBeton;

  @JsonProperty("repartition_departement_viab")
  private Integer repartitionDepartementViabilite;

  private Date dateMaj;
}

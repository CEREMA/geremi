package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MateriauDTO {
  @JsonProperty("id_materiau")
  private Integer idMateriau;
  @JsonProperty("libelle")
  private String libelle;
  @JsonProperty("type")
  private String type;
  @JsonProperty("origine")
  private String origine;
  @JsonProperty("tonnage")
  private Integer tonnage;
  @JsonProperty("id_etude")
  private Long idEtude;
}

package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZoneProductionDetailsDTO {
  
  @JsonProperty("id")  
  private Long id;
  
  @JsonProperty("name")
  private String name;
  
  @JsonProperty("tons")
  private Long tons;
  
  @JsonProperty("pourcentage1")
  private Long pourcentage1;

  @JsonProperty("pourcentage2")
  private Long pourcentage2;
}

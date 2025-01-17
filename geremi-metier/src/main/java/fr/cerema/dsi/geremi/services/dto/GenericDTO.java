package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class GenericDTO {

  @JsonProperty("id")
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}

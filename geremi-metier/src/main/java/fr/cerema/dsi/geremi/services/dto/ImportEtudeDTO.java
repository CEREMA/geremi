package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.util.List;

@Data
public class ImportEtudeDTO {
  @Serial
  private static final long serialVersionUID = 5667250033525120405L;

  @JsonProperty("etude")
  private EtudeDTO etude;
  @JsonProperty("scenario")
  private ScenarioDTO scenario;
}

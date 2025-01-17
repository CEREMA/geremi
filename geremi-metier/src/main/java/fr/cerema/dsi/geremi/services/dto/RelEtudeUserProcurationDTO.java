package fr.cerema.dsi.geremi.services.dto;

import java.io.Serial;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelEtudeUserProcurationDTO extends GenericDTO {

	@Serial
  private static final long serialVersionUID = 7603920734942292368L;

	@JsonProperty("id")
	private Long id;

	@JsonProperty("etude")
	private EtudeDTO etudeDTO;

	@JsonProperty("user")
	private UserDTO userDTO;

	@JsonProperty("ifDroitEcriture")
	private Boolean ifDroitEcriture;

}

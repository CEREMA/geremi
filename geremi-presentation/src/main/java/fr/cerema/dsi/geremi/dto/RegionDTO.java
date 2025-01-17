package fr.cerema.dsi.geremi.dto;


import java.io.Serial;

import fr.cerema.dsi.geremi.services.dto.GenericDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionDTO extends GenericDTO {

    /**
	 *
	 */
	@Serial
  private static final long serialVersionUID = 8603036434942947368L;

	private String code;
    private String nom;
}

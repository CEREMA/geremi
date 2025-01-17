package fr.cerema.dsi.geremi.dto;


import java.io.Serial;

import fr.cerema.dsi.geremi.services.dto.GenericDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilDTO extends GenericDTO {

    /**
	 *
	 */
	@Serial
  private static final long serialVersionUID = -6610653511904929914L;

    private String libelle;

    // getters and setters
}

package fr.cerema.dsi.geremi.dto;

import java.io.Serial;

import fr.cerema.dsi.geremi.services.dto.GenericDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefEtatDTO  extends GenericDTO {


    /**
	 *
	 */
    @Serial
	private static final long serialVersionUID = -4342608977233118034L;
	private String libelle;

}

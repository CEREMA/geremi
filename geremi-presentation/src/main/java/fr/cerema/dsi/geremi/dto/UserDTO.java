package fr.cerema.dsi.geremi.dto;

import fr.cerema.dsi.geremi.services.dto.GenericDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO extends GenericDTO {

	private String nom;

	private String prenom;

	private String mail;

  private String etat;

  private String profil;

}

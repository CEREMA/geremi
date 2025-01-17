package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class UserDTO {

  @JsonProperty("id_user")
  private Long id;

  @JsonProperty("nom")
  private String nom;

  @JsonProperty("prenom")
  private String prenom;

  @JsonProperty("mail")
  private String mail;

  @JsonProperty("id_etat")
  private Long idEtat;

  @JsonProperty("libelle_etat")
  private String libelleEtat;

  @JsonProperty("id_profil")
  private Long idProfil;

  @JsonProperty("libelle_profil")
  private String libelleProfil;

  @JsonProperty("id_region")
  private Long idRegion;

  @JsonProperty("insee_region")
  private String inseeRegion;

  @JsonProperty("nom_region")
  private String nomRegion;

  @JsonProperty("if_droit_ecriture")
  private boolean ifDroitEcriture;
}

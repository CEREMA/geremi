package fr.cerema.dsi.geremi.services.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class EtudesUtilisateurDTO {

  @JsonProperty("etudesProprietaire")
  private List<EtudeDTO> etudesProprietaire;

  @JsonProperty("etudesMandataireLectureSeule")
  private List<EtudeDTO> etudesMandataireLectureSeule;

  @JsonProperty("etudesMandataireEcriture")
  private List<EtudeDTO> etudesMandataireEcriture;

  @JsonProperty("etudesPublique")
  private List<EtudeDTO> etudesPublique;

  @JsonProperty("etudesImporte")
  private List<EtudeDTO> etudesImporte;
}

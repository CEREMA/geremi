package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopulationDTO {
 

    @JsonProperty("id_population")
    private Long idPopulation;

    @JsonProperty("codeZone")
    private String codeZone;

    @JsonProperty("nomZone")
    private String nomZone;

    @JsonProperty("annee")
    private Integer annee;

    @JsonProperty("populationBasse")
    private Integer populationBasse;

    @JsonProperty("populationCentrale")
    private Integer populationCentrale;

    @JsonProperty("populationHaute")
    private Integer populationHaute;

    @JsonProperty("idZone")
    private Long idZone;

    @JsonProperty("idEtude")
    private Long idEtude;

	public PopulationDTO(String codeZone, String nomZone, int annee, int populationBasse, int populationCentrale,
			int populationHaute, Long idZone,  Long idEtude) {
		this.codeZone = codeZone;
		this.nomZone = nomZone;
		this.annee = annee;
		this.populationBasse = populationBasse;
		this.populationCentrale = populationCentrale;
		this.populationHaute = populationHaute;
		this.idZone = idZone;
		this.idEtude = idEtude;
	}
}

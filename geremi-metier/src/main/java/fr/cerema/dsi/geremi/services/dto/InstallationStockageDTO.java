package fr.cerema.dsi.geremi.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstallationStockageDTO {

	@JsonProperty("nomEtab")
	private String nomEtab;

	@JsonProperty("description")
	private String description;

	@JsonProperty("anneeDebut")
	private Integer anneeDebut;

	@JsonProperty("anneeFin")
	private Integer anneeFin;

	@JsonProperty("betonPref")
	private Integer betonPref;

	@JsonProperty("viabAutre")
	private Integer viabAutre;

	@JsonProperty("tonTot")
	private Integer tonTot;

  @JsonProperty("idSource")
  private Integer idSource;

  @JsonProperty("libellePere")
  private String LibellePere;

	public InstallationStockageDTO(String nomEtab, String description, Integer anneeDebut, Integer anneeFin,
			Integer betonPref, Integer viabAutre, Integer tonTot) {
		this.nomEtab = nomEtab;
		this.description = description;
		this.anneeDebut = anneeDebut;
		this.anneeFin = anneeFin;
		this.betonPref = betonPref;
		this.viabAutre = viabAutre;
		this.tonTot = tonTot;
	}
}

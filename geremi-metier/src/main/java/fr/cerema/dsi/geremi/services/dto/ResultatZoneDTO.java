package fr.cerema.dsi.geremi.services.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultatZoneDTO {

	private Long idEtude;
	private Long idScenario;
  private Long idZone;
	private int annee;

	private Double besoinZoneTotalChantier;
	private Double besoinZonePrimaire;
	private Double besoinZoneSecondaire;
	private Double besoinZoneTotal;

	private Double productionZonePrimaireTotal;
  private Double productionZonePrimaireIntra;
  private Double productionZonePrimaireBrute;
	private Double productionZoneSecondaireTotal;
	private Double productionZoneTotal;

	private Double pourcentageProductionZoneSecondaire;

  private Double productionZonePrimaireTotalReelle;
  private Double productionZonePrimaireBruteReelle;

	public ResultatZoneDTO(Long idEtude, Long idScenario, int annee,Long idZone) {
		this.idEtude = idEtude;
		this.idScenario = idScenario;
		this.idZone = idZone;
		this.annee = annee;
	}
}

package fr.cerema.dsi.geremi.services.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductionZoneDto {

	private Long idEtude;
	private Long idScenario;
  private Long idZone;
	private int anneeRef;
	private BigDecimal productionZoneBeton = BigDecimal.ZERO;
	private BigDecimal productionZoneViab = BigDecimal.ZERO;
	private BigDecimal productionZonePrimaire = BigDecimal.ZERO;
	private BigDecimal productionZonePrimaireAutre = BigDecimal.ZERO;
  private BigDecimal productionZonePrimaireIntra = BigDecimal.ZERO;
  private BigDecimal productionZonePrimaireBrute = BigDecimal.ZERO;
	private BigDecimal productionZoneSecondaireAutre = BigDecimal.ZERO;
	private BigDecimal productionZoneStock = BigDecimal.ZERO;
	private BigDecimal productionZonePrimaireTotal = BigDecimal.ZERO;
	private BigDecimal productionZoneSecondaireTotal = BigDecimal.ZERO;
	private BigDecimal productionZoneTotal = BigDecimal.ZERO;
	private BigDecimal pourcentageProductionZoneSecondaire = BigDecimal.ZERO;

	public ProductionZoneDto(Long idEtude, Long idScenario, Long idZone, int anneeRef) {
		this.idEtude = idEtude;
		this.idScenario = idScenario;
		this.idZone = idZone;
		this.anneeRef = anneeRef;
	}
}

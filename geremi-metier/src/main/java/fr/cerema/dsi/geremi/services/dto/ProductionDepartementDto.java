package fr.cerema.dsi.geremi.services.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductionDepartementDto {

	private Long idEtude;
	private Long idScenario;
	private Long idDepartement;
	private int anneeRef;
	private BigDecimal productionDepartementBeton = BigDecimal.ZERO;
	private BigDecimal productionDepartementViab = BigDecimal.ZERO;

	private List<ProductionZoneDto> listProductionZoneDto = new ArrayList<>();

	public ProductionDepartementDto(Long idEtude, Long idScenario, Long idDepartement, int anneeRef,
			BigDecimal productionDepartementBeton, BigDecimal productionDepartementViab) {
		this.idEtude = idEtude;
		this.idScenario = idScenario;
		this.idDepartement = idDepartement;
		this.anneeRef = anneeRef;
		this.productionDepartementBeton = productionDepartementBeton;
		this.productionDepartementViab = productionDepartementViab;
	}
}

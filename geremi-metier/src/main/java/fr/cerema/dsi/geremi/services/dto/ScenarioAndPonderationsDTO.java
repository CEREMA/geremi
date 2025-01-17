package fr.cerema.dsi.geremi.services.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ScenarioAndPonderationsDTO {
	private ScenarioDTO scenario;
	private BigDecimal PRSB;
	private BigDecimal PRPB;
	private BigDecimal PRSV;
	private BigDecimal PRPV;
}
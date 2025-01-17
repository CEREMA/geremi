package fr.cerema.dsi.geremi.services.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ZoneDetailsDTO {
    private Long id;
    private BigDecimal besoinTotalChantierZoneAnnee;
    private List<ChantierDetailsDTO> chantierDetails;
}
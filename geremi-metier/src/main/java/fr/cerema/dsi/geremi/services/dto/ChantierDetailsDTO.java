package fr.cerema.dsi.geremi.services.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ChantierDetailsDTO {
    private BigDecimal besoinChantierZoneBeton;
    private BigDecimal besoinChantierZoneViab;
    private BigDecimal besoinChantierZoneTot;
    private BigDecimal besoinChantierZone;
    private BigDecimal besoinChantierZoneAnnuel;
}
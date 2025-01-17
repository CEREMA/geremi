package fr.cerema.dsi.geremi.services.dto;

import lombok.Data;

@Data
public class TauxCouvertureDTO {
  private Long idTerritoire;
  private Double tauxHorsCouvertureAccepte;
  private Double tauxHorsCouvertureCalcule;

  private Double tauxNonCouvertAccepte;
  private Double tauxNonCouvertCalcule;
}

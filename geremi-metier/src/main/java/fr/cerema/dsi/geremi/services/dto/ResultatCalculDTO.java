package fr.cerema.dsi.geremi.services.dto;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultatCalculDTO {

  private Long idEtude;
  private Long idScenario;
  private Integer annee;


  private Double besoinTerritoireTotalChantier;
  private Double besoinTerritoirePrimaire;
  private Double besoinTerritoireSecondaire;
  private Double besoinTerritoireTotal;

  private Double productionTerritoirePrimaireTotal;
  private Double productionTerritoirePrimaireIntra;
  private Double productionTerritoirePrimaireBrute;
  private Double productionTerritoireSecondaireTotal;
  private Double productionTerritoireTotal;

  private Double pourcentProductionTerritoireSecondaire;

  //map par zone
  private Map<Long, ResultatZoneDTO> resultatZones;

  public ResultatCalculDTO(Long idEtude, Long idScenario, int annee) {
    this.idEtude = idEtude;
    this.idScenario = idScenario;
    this.annee = annee;
  }
}

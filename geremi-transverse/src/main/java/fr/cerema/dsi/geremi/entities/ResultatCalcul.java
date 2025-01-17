package fr.cerema.dsi.geremi.entities;

import java.time.LocalDateTime;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "resultat_calcul")
@Getter
public class ResultatCalcul extends GenericEntity {

  /**
	 *
	 */
	private static final long serialVersionUID = -606486779944949646L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_resultat")
  private Long id;
  @Column(name = "id_scenario")
  private Long idScenario;
  @Column(name = "id_territoire")
  private Long idTerritoire;
  @Column(name = "annee")
  private Integer annee;

  @Column(name = "if_projection")
  private Boolean ifProjection;

  @Column(name = "besoin_territoire_total_chantier")
  private Double besoinTerritoireTotalChantier;
  @Column(name = "besoin_territoire_primaire")
  private Double besoinTerritoirePrimaire;
  @Column(name = "besoin_territoire_secondaire")
  private Double besoinTerritoireSecondaire;
  @Column(name = "besoin_territoire_total")
  private Double besoinTerritoireTotal;

  @Column(name = "production_territoire_primaire")
  private Double productionTerritoirePrimaire;
  @Column(name = "production_territoire_secondaire")
  private Double productionTerritoireSecondaire;
  @Column(name = "production_territoire_total")
  private Double productionTerritoireTotal;
  @Column(name = "production_territoire_primaire_intra")
  private Double productionTerritoirePrimaireIntra;
  @Column(name = "production_territoire_primaire_brut")
  private Double productionTerritoirePrimaireBrut;

  @Column(name = "pourcent_production_territoire_secondaire")
  private Double pourcentProductionTerritoireSecondaire;

  @Column(name = "date_maj", nullable = false)
  private LocalDateTime dateMaj;
}

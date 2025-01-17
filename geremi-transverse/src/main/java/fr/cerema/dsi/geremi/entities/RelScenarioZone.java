package fr.cerema.dsi.geremi.entities;

import java.time.LocalDateTime;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "rel_scenario_zone", schema = "geremi")
@Getter
@Setter
public class RelScenarioZone extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -7633360808097541770L;

  @Id
	@SequenceGenerator(name = "rel_scenario_zone_id_seq", sequenceName = "geremi.rel_scenario_zone_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "rel_scenario_zone_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_rel_scenario_zone", nullable = false)
	private Long idRelScenarioZone;

	@Column(name = "id_scenario", nullable = false)
	private Long idScenario;

	@Column(name = "id_zone", nullable = false)
	private Long idZone;

	@Column(name = "projection_secondaire_echeance", nullable = false)
	private Long projectionScondaireEcheance;

  @Column(name = "date_maj", nullable = false)
  private LocalDateTime dateMaj;

}

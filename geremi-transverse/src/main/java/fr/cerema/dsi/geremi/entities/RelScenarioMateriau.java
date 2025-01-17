package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "rel_scenario_materiau", schema = "geremi")
@Getter
@Setter
public class RelScenarioMateriau extends GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1714862357208677155L;

	@Id
	@SequenceGenerator(name = "rel_scenario_materiau_id_seq", sequenceName = "geremi.rel_scenario_materiau_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "rel_scenario_materiau_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_rel_scenario_materiau", nullable = false)
	private Integer idRelScenarioMateriau;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_scenario", nullable = false)
	@ToString.Exclude
	private Scenario scenario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_materiau", nullable = false)
	@ToString.Exclude
	private Materiau materiau;
	
	@Column(name = "tonnage", nullable = false)
	private Integer tonnage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_zone", nullable = false)
	@ToString.Exclude
	private Zone zone;
}

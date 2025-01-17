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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Entity
@Table(name = "rel_scenario_installation")
@Getter
@Setter
public class ScenarioInstallation extends GenericEntity {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Id
  @SequenceGenerator(name = "rel_scenario_installation_id_seq", sequenceName = "rel_scenario_installation_id_seq", allocationSize = 1)
  @GeneratedValue(generator = "rel_scenario_installation_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id_rel_scenario_installation")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_scenario", nullable = false)
  @ToString.Exclude
  private Scenario scenario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_installation", nullable = false)
  @ToString.Exclude
  private InstallationStockage installation;
}

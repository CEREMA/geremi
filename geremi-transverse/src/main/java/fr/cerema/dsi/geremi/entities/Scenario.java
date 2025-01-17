package fr.cerema.dsi.geremi.entities;


import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.Hibernate;

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
@Table(name = "scenario")
@Getter
@Setter
public class Scenario extends GenericEntity {

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 9067719722999933624L;

	@Id
	@SequenceGenerator(name = "scenario_id_seq", sequenceName = "scenario_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "scenario_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_scenario")
	private Long id;

	@Column(name = "nom", nullable = false)
	private String nom;

	@Column(name = "description")
  private String description;

	@Column(name = "dynamique_demographique")
	private String dynamiqueDemographique;

	@Column(name = "tx_renouvellement_hc")
	private Integer txRenouvellementHc;

	@Column(name = "ponderation_surface_beton")
	private Integer ponderationSurfaceBeton;

	@Column(name = "ponderation_surface_viabilite")
	private Integer ponderationSurfaceViabilite;

	@Column(name = "if_retenu", nullable = false)
	private Boolean ifRetenu;

	@Column(name = "date_maj", nullable = false)
	private LocalDateTime dateMaj;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_etude", nullable = false)
	@ToString.Exclude
	private Etude etude;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		Scenario etude = (Scenario) o;
		return id != null && Objects.equals(id, etude.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}

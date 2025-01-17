package fr.cerema.dsi.geremi.entities;

import org.locationtech.jts.geom.Geometry;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "territoire", schema = "geremi")
@Getter
@Setter
public class Territoire extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -382006997053929949L;

	@Id
	@SequenceGenerator(name = "territoire_id_seq", sequenceName = "territoire_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "territoire_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_territoire")
	private Long id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "nom", nullable = false)
	private String nom;

	@Column(name = "description")
	private String description;

  @Column(name = "liste_territoire")
  private String listeTerritoire;

	@Column(name = "the_geom", columnDefinition = "geometry", nullable = false)
	private Geometry geometry;

	@OneToOne
	@JoinColumn(name = "id_etude", nullable = false)
	private Etude etude;

}

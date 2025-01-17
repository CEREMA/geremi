package fr.cerema.dsi.geremi.entities;

import org.locationtech.jts.geom.Geometry;

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

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "zone")
@Getter
@Setter
public class Zone extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -1156134405776451697L;

	@Id
	@SequenceGenerator(name = "zone_id_seq", sequenceName = "zone_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "zone_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_zone")
	private Long id;

	@Column(name = "type")
	private String type;

	@Column(name = "nom")
	private String nom;

	@Column(name = "description")
	private String description;

	@Column(name = "code")
	private String code;

	@Column(name = "the_geom")
	private Geometry theGeom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_etude", nullable = false)
	private Etude etude;
}

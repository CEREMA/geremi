package fr.cerema.dsi.geremi.entities;

import org.locationtech.jts.geom.Geometry;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "geometry")
@Entity(name = "epci_geometry")
@Table(name = "ref_epci")
@Getter
@Setter
public class EpciGeometry extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -8864737498967820113L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)")
	private Geometry geometry;

}

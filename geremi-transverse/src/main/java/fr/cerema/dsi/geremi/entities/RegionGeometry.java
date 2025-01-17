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
@Entity(name = "region_geometry")
@Table(name = "region_geometry")
@Getter
@Setter
public class RegionGeometry extends GenericEntity {

  /**
	 *
	 */
	private static final long serialVersionUID = -1378228763191938773L;

@Id
  @Column(name = "id")
  private Long id;

  @Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)")
  private Geometry geometry;
}

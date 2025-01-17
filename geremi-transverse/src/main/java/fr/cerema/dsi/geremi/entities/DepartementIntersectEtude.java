package fr.cerema.dsi.geremi.entities;

import org.locationtech.jts.geom.Geometry;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "deptGeom")
@Entity(name = "DepartementIntersectEtude")
@Table(schema = "geremi", name = "v_departement_intersect_etude")
public class DepartementIntersectEtude extends GenericEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_etude")
	private Long idEtude;

	@Id
	@Column(name = "id_departement")
	private Long idDepartement;

	@Column(name = "nom_departement")
	private String nomDepartement;

	@Column(name = "dept_geom", columnDefinition = "geometry(Geometry, 4326)")
	private Geometry deptGeom;

}

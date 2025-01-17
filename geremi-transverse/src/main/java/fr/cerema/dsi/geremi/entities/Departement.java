package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "region")
@Entity(name = "departement")
@Table(name = "ref_departement")
public class Departement extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -1864892075596838919L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "insee_departement")
	private String code;

	@Column(name = "nom_departement")
	private String nom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_region")
	private Region region;

}

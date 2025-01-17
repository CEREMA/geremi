package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity(name = "region")
@Table(name = "ref_region")
@Getter
@Setter
public class Region extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -2656874166798606968L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "insee_region")
	private String code;

	@Column(name = "nom_region")
	private String nom;

}

package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity(name = "epci")
@Table(name = "ref_epci")
public class Epci extends GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7030667439517851411L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "siren_epci")
	private String sirenEpci;

	@Column(name = "nom_epci")
	private String nomEpci;

	@Column(name = "nature_epci")
	private String natureEpci;

}

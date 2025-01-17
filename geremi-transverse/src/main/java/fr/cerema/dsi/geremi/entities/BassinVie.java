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
@Entity(name = "bassin_vie")
@Table(name = "ref_bassin_vie")
public class BassinVie extends GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8910240658823036643L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "code_bassin_vie")
	private String code;

	@Column(name = "nom_bassin_vie")
	private String nom;

}

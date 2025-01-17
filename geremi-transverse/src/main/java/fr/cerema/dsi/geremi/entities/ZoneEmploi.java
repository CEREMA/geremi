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
@Entity(name = "zoneEmploi")
@Table(name = "ref_zone_emploi")
@Getter
@Setter
public class ZoneEmploi extends GenericEntity {

  /**
	 * 
	 */
	private static final long serialVersionUID = -801004374645072228L;

@Id
  @Column(name = "id")
  private Long id;

  @Column(name = "code_zone_emploi")
  private String code;

  @Column(name = "nom_zone_emploi")
  private String nom;
}

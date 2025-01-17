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
@Entity(name = "declaration")
@Table(name = "declaration")
public class Declaration extends GenericEntity {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -886629354418420844L;

	@Id
    @Column(name = "id_declaration")
    private Integer id;

    @Column(name = "id_etab")
    private Integer id_etab;

    @Column(name = "nom_etab")
    private String nom_etab;

    @Column(name = "code_etab")
    private String code_etab;

    @Column(name = "annee")
    private String annee;

    @Column(name = "carriere")
    private String carriere;
}

package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "ref_etat")
@Getter
@Setter
public class RefEtat extends GenericEntity{

    /**
	 *
	 */
	private static final long serialVersionUID = 5061745448869248543L;

	@Id
    @SequenceGenerator(name = "ref_etat_id_seq", sequenceName = "ref_etat_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "ref_etat_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_ref_etat")
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

}

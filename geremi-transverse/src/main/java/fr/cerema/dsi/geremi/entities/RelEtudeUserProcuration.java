package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "rel_etude_user_procuration")
@Getter
@Setter
public class RelEtudeUserProcuration extends GenericEntity{

    /**
	 *
	 */
	private static final long serialVersionUID = -637072509546567820L;

	@Id
    @SequenceGenerator(name = "rel_etude_user_procuration_id_seq", sequenceName = "rel_etude_user_procuration_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "rel_etude_user_procuration_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_rel_etude_user")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etude", nullable = false)
    private Etude etude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    private User user;

    @Column(name = "if_droit_ecriture", nullable = false)
    private Boolean ifDroitEcriture;


}

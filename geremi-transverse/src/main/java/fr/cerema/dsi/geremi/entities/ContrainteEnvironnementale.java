package fr.cerema.dsi.geremi.entities;

import org.locationtech.jts.geom.Geometry;

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
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "contrainte_environnementale", schema = "geremi")
@Getter
@Setter
public class ContrainteEnvironnementale extends GenericEntity {

    /**
	 *
	 */
	private static final long serialVersionUID = 3515244135467498264L;


	@Id
    @SequenceGenerator(name = "contrainte_environnementale_id_seq", sequenceName = "geremi.contrainte_environnementale_id_contr_env_seq", allocationSize = 1)
    @GeneratedValue(generator = "contrainte_environnementale_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_contr_env")
    private Integer idContrEnv;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "niveau")
    private String niveau;

    @Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)", nullable = false)
    private Geometry theGeom;

    @Column(name = "id_source")
    private Integer idSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etude", nullable = false)
    @ToString.Exclude private Etude etude;

}

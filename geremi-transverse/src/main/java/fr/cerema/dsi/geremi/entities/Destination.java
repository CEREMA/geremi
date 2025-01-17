package fr.cerema.dsi.geremi.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "destination", schema = "geremi")
public class Destination extends GenericEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4230474908969762306L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_destination")
    private Long idDestination;

    @Column(name = "id_etablissement")
    private Integer idEtablissement;

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "famille_rattachement_destination")
    private String familleRattachementDestination;

    @Column(name = "type_produit_destination")
    private String typeProduitDestination;

    @Column(name = "libelle_destination")
    private String libelleDestination;

    @Column(name = "tonnage_destination")
    private BigDecimal tonnageDestination;

    @Column(name = "date_maj")
    private LocalDateTime dateMaj;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etablissement", insertable = false, updatable = false)
    private Etablissement etablissement;
}

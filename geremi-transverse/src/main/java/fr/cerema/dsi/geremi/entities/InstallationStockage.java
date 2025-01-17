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
@Table(name = "installation_stockage", schema = "geremi")
@Getter
@Setter
public class InstallationStockage extends GenericEntity {

    private static final long serialVersionUID = 1L;

	  @Id
    @SequenceGenerator(name = "installation_stockage_id_seq", sequenceName = "geremi.installation_stockage_id_stockage_seq", allocationSize = 1)
    @GeneratedValue(generator = "installation_stockage_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_stockage")
    private Integer idStockage;

    @Column(name = "nom_etab", nullable = false)
    private String nomEtab;

    @Column(name = "code_etab")
    private String codeEtab;

    @Column(name = "description")
    private String description;

    @Column(name = "annee_debut", nullable = false)
    private Integer anneeDebut;

    @Column(name = "annee_fin", nullable = false)
    private Integer anneeFin;

    @Column(name = "beton_pref")
    private Integer betonPref;

    @Column(name = "viab_autre")
    private Integer viabAutre;

    @Column(name = "ton_tot", nullable = false)
    private Integer tonTot;

    @Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)", nullable = false)
    private Geometry theGeom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source")
    @ToString.Exclude private InstallationStockage installationStockageSource;

    @Column(name = "id_frere")
    private Integer idFrere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etude", nullable = false)
    @ToString.Exclude private Etude etude;
}

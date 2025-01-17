package fr.cerema.dsi.geremi.entities;

import java.time.LocalDate;

import org.locationtech.jts.geom.Geometry;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Cette classe correspond a un établissement.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "geometry")
@Entity(name = "etablissement")
@Table(name = "etablissement")
@Getter
@Setter
public class Etablissement extends GenericEntity {

    private static final long serialVersionUID = -8459016862503053782L;

    @Id
    @Column(name = "id_etab")
    private Integer id;

    @Column(name = "annee")
    private String annee;

    @Column(name = "code_etab")
    private String code;

    @Column(name = "nom_etablissement")
    private String nom;

    @Column(name = "libelle_adresse")
    private String adresse;

    @Column(name = "code_postal_site")
    private Integer codePostal;

    @Column(name = "libelle_commune")
    private String commune;

    @Column(name = "code_ape")
    private String codeApe;

    @Column(name = "activite_principale")
    private String activite;

    @Column(name = "type_produit")
    private String typeProduits;

    @Column(name = "volume_production")
    private String volumeProduction;

    @Column(name = "unite")
    private String unite;

    @Column(name = "siret")
    private Long siret;

    @Column(name = "site_internet")
    private String siteInternet;

    @Column(name = "nb_employe")
    private Integer nbEmploye;

    @Column(name = "long")
    private String longitude;

    @Column(name = "lat")
    private String latitude;

    @Column(name = "origin_mat")
    private String originMat;

    @Column(name = "commentaire_section")
    private String commentaireSection;

    @Column(name = "remarques")
    private String remarques;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "date_fin_autorisation")
    private LocalDate dateFinAutorisation;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)")
    private Geometry geometry;
}

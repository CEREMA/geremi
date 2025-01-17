package fr.cerema.dsi.geremi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EtablissementDTO {

    @JsonProperty("id_etab")
    private Integer id;

    @JsonProperty("code_etab")
    private String code;

    @JsonProperty("nom_etab")
    private String nom;

    @JsonProperty("code_postal")
    private String codePostal;

    @JsonProperty("adresse_site")
    private String adresse;

    @JsonProperty("commune")
    private String commune;

    @JsonProperty("code_ape")
    private String codeApe;

    @JsonProperty("activite_principale")
    private String activite;

    @JsonProperty("type_produits")
    private String typeProduits;

    @JsonProperty("volume_production")
    private String volumeProduction;

    @JsonProperty("unite")
    private String unite;

    @JsonProperty("siret")
    private Long siret;

    @JsonProperty("site_internet")
    private String siteInternet;

    @JsonProperty("remarques")
    private String remarques;

    @JsonProperty("systeme_geo")
    private String systeme_geo;
}

package fr.cerema.dsi.geremi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclarationDTO {

    @JsonProperty("id_declaration")
    private Integer id;

    @JsonProperty("id_etab")
    private Integer id_etab;

    @JsonProperty("nom_etab")
    private String nom_etab;

    @JsonProperty("code_etab")
    private String code_etab;

    @JsonProperty("annee")
    private String annee;

    @JsonProperty("carriere")
    private String carriere;
}

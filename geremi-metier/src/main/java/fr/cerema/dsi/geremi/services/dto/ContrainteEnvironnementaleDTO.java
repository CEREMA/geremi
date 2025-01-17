package fr.cerema.dsi.geremi.services.dto;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContrainteEnvironnementaleDTO {

    @JsonProperty("id_contr_env")
    private Integer idContrEnv;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("description")
    private String description;

    @JsonProperty("niveau")
    private String niveau;

    @JsonProperty("the_geom")
    private Geometry theGeom;

    @JsonProperty("id_source")
    private Integer idSource;

    @JsonProperty("etude")
    private EtudeDTO etudeDTO;

}

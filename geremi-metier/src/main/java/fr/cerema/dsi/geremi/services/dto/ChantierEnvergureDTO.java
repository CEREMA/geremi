package fr.cerema.dsi.geremi.services.dto;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChantierEnvergureDTO {
  @JsonProperty("id_chantier")
  private Integer idChantier;
  @JsonProperty("nom")
  private String nom;
  @JsonProperty("description")
  private String description;
  @JsonProperty("annee_debut")
  private Integer anneeDebut;
  @JsonProperty("annee_fin")
  private Integer anneeFin;
  @JsonProperty("beton_pref")
  private Integer betonPref;
  @JsonProperty("viab_autre")
  private Integer viabAutre;
  @JsonProperty("ton_tot")
  private Integer tonTot;
  @JsonProperty("the_geom")
  private Geometry theGeom;
  @JsonProperty("id_source")
  private Integer idSource;
  @JsonProperty("id_frere")
  private Integer idFrere;
  @JsonProperty("libelle_pere")
  private String libellePere;
  @JsonProperty("etude")
  private EtudeDTO etudeDTO;
}

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
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "chantier_envergure", schema = "geremi")
public class ChantierEnvergure extends GenericEntity {

  private static final long serialVersionUID = 3515244135467498265L;

  @Id
  @SequenceGenerator(name = "chantier_envergure_id_seq", sequenceName = "geremi.chantier_envergure_id_chantier_seq", allocationSize = 1)
  @GeneratedValue(generator = "chantier_envergure_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id_chantier")
  private Integer idChantier;
  @Column(name = "nom")
  private String nom;
  @Column(name = "description")
  private String description;
  @Column(name = "annee_debut")
  private Integer anneeDebut;
  @Column(name = "annee_fin")
  private Integer anneeFin;
  @Column(name = "beton_pref")
  private Integer betonPref;
  @Column(name = "viab_autre")
  private Integer viabAutre;
  @Column(name = "ton_tot")
  private Integer tonTot;
  @Column(name = "the_geom", columnDefinition = "geometry(Geometry, 4326)")
  private Geometry theGeom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_source", nullable = true)
  @ToString.Exclude private ChantierEnvergure chantierSource;

  @Column(name = "id_frere")
  private Integer idFrere;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_etude", nullable = false)
  @ToString.Exclude private Etude etude;
}

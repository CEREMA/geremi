package fr.cerema.dsi.geremi.entities;

import java.util.Date;

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
import lombok.Getter;
import lombok.Setter;


@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "rel_scenario_departement")
@Getter
@Setter
public class RelScenarioDepartement extends GenericEntity {

	private static final long serialVersionUID = -9188639427327822221L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_rel_scenario_departement")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_scenario", nullable = false)
  private Scenario scenario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_departement", nullable = false)
  private Departement departement;

  @Column(name = "repartition_departement_beton", nullable = false)
  private Integer repartitionDepartementBeton;

  @Column(name = "repartition_departement_viabilite", nullable = false)
  private Integer repartitionDepartementViabilite;

  @Column(name = "date_maj")
  private Date dateMaj;

}

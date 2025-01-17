package fr.cerema.dsi.geremi.entities;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.Hibernate;

import fr.cerema.dsi.commons.entities.GenericEntity;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Entity
@Table(name = "tracabilite_etape")
@Getter
@Setter
public class TracabiliteEtape extends GenericEntity {

	/**
	 *
	 */
	@Serial
  private static final long serialVersionUID = 9067725075670033624L;

	@Id
	@SequenceGenerator(name = "tracabilite_etape_id_seq", sequenceName = "tracabilite_etape_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "tracabilite_etape_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;

	@Column(name = "date_maj", nullable = false)
	private LocalDateTime dateMaj;

  @Column(name = "etape", nullable = false)
  private Etape etape;

  @Column(name = "action", nullable = false)
  private EtatEtape etat;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_etude", nullable = false)
  @ToString.Exclude
  private Etude etude;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_scenario")
  @ToString.Exclude
  private Scenario scenario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users", nullable = false)
  @ToString.Exclude
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    TracabiliteEtape tracabiliteEtape = (TracabiliteEtape) o;
    return id != null && Objects.equals(id, tracabiliteEtape.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

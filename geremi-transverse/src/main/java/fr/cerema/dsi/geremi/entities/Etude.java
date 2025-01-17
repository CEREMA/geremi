package fr.cerema.dsi.geremi.entities;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.hibernate.Hibernate;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Entity
@Table(name = "etude")
@Getter
@Setter
public class Etude extends GenericEntity {

	/**
	 *
	 */
	@Serial
  private static final long serialVersionUID = 9067725075999933624L;

	@Id
	@SequenceGenerator(name = "etude_id_seq", sequenceName = "etude_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "etude_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_etude")
	private Long id;

	@Column(name = "nom", nullable = false)
	private String nom;

	@Column(name = "description")
	private String description;

	@Column(name = "date_creation", nullable = false)
	private LocalDate dateCreation;

	@Column(name = "if_src", nullable = false)
	private boolean ifSrc;

	@Column(name = "annee_ref", nullable = false)
	private Integer anneeRef;

	@Column(name = "annee_fin")
	private Integer anneeFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_users", nullable = false)
  @ToString.Exclude
  private User user;

  @OneToMany(fetch = FetchType.LAZY, mappedBy="etude")
  @ToString.Exclude
  private List<RelEtudeUserProcuration> relEtudeUserProcuration;

	@Column(name = "if_public", nullable = false)
	private boolean ifPublic;

  @Column(name = "if_importe")
  private boolean ifImporte;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Etude etude = (Etude) o;
    return id != null && Objects.equals(id, etude.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

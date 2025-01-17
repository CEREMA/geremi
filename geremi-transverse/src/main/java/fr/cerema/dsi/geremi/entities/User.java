package fr.cerema.dsi.geremi.entities;


import java.time.LocalDate;

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

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
@Getter
@Setter
public class User extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 9186476479655774947L;

	@Id
	@SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "users_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_users")
	private Long id;

	@Column(name = "nom", nullable = false)
	private String nom;

	@Column(name = "prenom", nullable = false)
	private String prenom;

	@Column(name = "mail", nullable = false)
	private String mail;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ref_etat", nullable = false)
	private RefEtat refEtat;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_profil", nullable = false)
	private Profil profil;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ref_region")
	private Region refRegion;

	@Column(name = "date_creation", nullable = false)
	private LocalDate dateCreation;

}

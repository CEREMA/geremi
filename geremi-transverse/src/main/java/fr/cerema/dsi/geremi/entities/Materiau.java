package fr.cerema.dsi.geremi.entities;

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
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "materiau", schema = "geremi")
@Getter
@Setter
public class Materiau extends GenericEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7633360808014486770L;

	@Id
	@SequenceGenerator(name = "materiau_id_seq", sequenceName = "geremi.materiau_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "materiau_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_materiau", nullable = false)
	private Integer idMateriau;
	
	@Column(name = "libelle", nullable = false)
	private String libelle;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@Column(name = "origine", nullable = false)
	private String origine;
	
	@Column(name = "tonnage", nullable = false)
	private Integer tonnage;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_etude")
	@ToString.Exclude
	private Etude etude;
}

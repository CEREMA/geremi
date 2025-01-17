package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude={"departement", "epci", "bassinVie", "zoneEmploi"})
@EqualsAndHashCode(callSuper = false)
@Entity(name = "commune")
@Table(name = "ref_commune")
public class Commune extends GenericEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -6931235413840850965L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "insee_commune")
	private String inseeCommune;

	@Column(name = "nom_commune")
	private String nomCommune;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_departement", foreignKey = @ForeignKey(name = "fk_ref_commune_ref_departement"))
	private Departement departement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_epci", foreignKey = @ForeignKey(name = "fk_ref_commune_ref_epci"))
	private Epci epci;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bassin_vie", foreignKey = @ForeignKey(name = "fk_ref_commune_ref_bassin_vie"))
	private BassinVie bassinVie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_zone_emploi", foreignKey = @ForeignKey(name = "fk_ref_commune_ref_zone_emploi"))
	private ZoneEmploi zoneEmploi;

}

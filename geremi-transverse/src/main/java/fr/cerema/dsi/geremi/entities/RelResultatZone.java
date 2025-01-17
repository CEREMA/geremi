package fr.cerema.dsi.geremi.entities;

import java.time.LocalDateTime;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "rel_resultat_zone")
@Getter
public class RelResultatZone extends GenericEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4513947807091340608L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_rel_resultat_zone")
	private Long id;
	@Column(name = "id_resultat")
	private Long idResultat;
	@Column(name = "id_zone")
	private Long idZone;
	@Column(name = "besoin_zone_total_chantier")
	private Double besoinZoneTotalChantier;
	@Column(name = "besoin_zone_primaire")
	private Double besoinZonePrimaire;
	@Column(name = "besoin_zone_secondaire")
	private Double besoinZoneSecondaire;
	@Column(name = "besoin_zone_total")
	private Double besoinZoneTotal;
	@Column(name = "production_zone_primaire")
	private Double productionZonePrimaire;
	@Column(name = "production_zone_secondaire")
	private Double productionZoneSecondaire;
	@Column(name = "production_zone_total")
	private Double productionZoneTotal;
	@Column(name = "production_zone_primaire_intra")
	private Double productionZonePrimaireIntra;
	@Column(name = "production_zone_primaire_brut")
	private Double productionZonePrimaireBrut;
	@Column(name = "pourcent_production_zone_secondaire")
	private Double pourcentProductionZoneSecondaire;
	@Column(name = "date_maj", nullable = false)
	private LocalDateTime dateMaj;
}

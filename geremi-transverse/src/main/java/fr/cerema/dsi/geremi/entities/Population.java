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
@Table(name = "population", schema = "geremi")
@Getter
@Setter
public class Population extends GenericEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "population_id_seq", sequenceName = "geremi.id_population_seq", allocationSize = 1)
    @GeneratedValue(generator = "population_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_population")
    private Integer idPopulation;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "population_basse", nullable = false)
    private Integer populationBasse;

    @Column(name = "population_centrale", nullable = false)
    private Integer populationCentrale;

    @Column(name = "population_haute", nullable = false)
    private Integer populationHaute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone", nullable = false)
    @ToString.Exclude private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etude", nullable = false)
    @ToString.Exclude private Etude etude;
}

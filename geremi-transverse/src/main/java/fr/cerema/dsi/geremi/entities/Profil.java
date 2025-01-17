package fr.cerema.dsi.geremi.entities;

import fr.cerema.dsi.commons.entities.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "profil")
@Getter
@Setter
public class Profil extends GenericEntity{


	@Id
    @SequenceGenerator(name = "profil_id_seq", sequenceName = "profil_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "profil_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_profil")
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

}

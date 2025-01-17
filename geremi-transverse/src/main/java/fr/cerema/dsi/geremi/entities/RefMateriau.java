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
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "ref_materiaux", schema = "geremi")
@Getter
@Setter
public class RefMateriau extends GenericEntity {
  @Id
  @SequenceGenerator(name = "ref_materiau_id_seq", sequenceName = "geremi.ref_materiau_id_seq", allocationSize = 1)
  @GeneratedValue(generator = "ref_materiau_id_seq", strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;
  @Column(name = "libelle", nullable = false)
  private String libelle;
  @Column(name = "type", nullable = false)
  private String type;
  @Column(name = "origine", nullable = false)
  private String origine;
}

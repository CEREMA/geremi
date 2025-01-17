package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RefMateriau;

public interface RefMateriauRepository extends GenericRepository<RefMateriau, Integer> {
  @Query(value = "SELECT * FROM ref_materiaux",nativeQuery = true)
  List<RefMateriau> getMateriauxDisponibles();
}

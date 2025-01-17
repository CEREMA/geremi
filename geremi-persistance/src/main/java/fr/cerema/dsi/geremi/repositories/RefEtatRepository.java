package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RefEtat;

public interface RefEtatRepository extends GenericRepository<RefEtat, Long> {

  List<RefEtat> findByLibelle(String etat);

}


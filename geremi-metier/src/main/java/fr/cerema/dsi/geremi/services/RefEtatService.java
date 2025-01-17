package fr.cerema.dsi.geremi.services;

import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.RefEtat;

public interface RefEtatService extends GenericService<RefEtat, Long> {

  List<RefEtat> findByLibelle(String etat);
}

package fr.cerema.dsi.geremi.services;

import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.RelResultatZone;

public interface RelResultatZoneService extends GenericService<RelResultatZone,Long> {
  List<RelResultatZone> findByIdResultat(Long idResultat);
  void delete(RelResultatZone entity);

  void delete(List<RelResultatZone> entities);


}

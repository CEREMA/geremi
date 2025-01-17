package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.services.dto.TauxCouvertureDTO;
import fr.cerema.dsi.geremi.services.dto.TerritoireDto;
import fr.cerema.dsi.geremi.services.dto.ZoneDTO;
import fr.cerema.dsi.geremi.services.errors.TauxHorsTerritoireException;
import org.locationtech.jts.geom.Geometry;

public interface TerritoireService extends GenericService<Territoire, Long> {
  Optional<String> findTypeZonageByIdEtude(Etude etude);
  Territoire validationCreationTerritoire(TerritoireDto territoireDto);
  Territoire findTerritoireByEtude(Etude etude);
  Territoire createTerritoire(TerritoireDto territoire);
  List<Zone> getZoneForEtude(Long id,String precision);
  List<ZoneDTO> findZoneByEtude(Long id);
  void createTerritoireFromImport(DataStore dataStore,Long idEtude);
  Territoire findByIdWithPrecision(Long id, String precision);
  TauxCouvertureDTO findTauxTerritoireHorsFrance(Long idEtude) throws TauxHorsTerritoireException;
  TauxCouvertureDTO findTauxTerritoireHorsRegion(Long idEtude, Long idRegion) throws TauxHorsTerritoireException;
  TauxCouvertureDTO findTauxCouvertureRegion(Long idEtude, Long idRegion) throws TauxHorsTerritoireException;
  TauxCouvertureDTO calculTauxCouverture(Long idEtude) throws TauxHorsTerritoireException;
  boolean geometryIsInTerritoire(Geometry geometry, Long idEtude);
  boolean geometryIntersectsTerritoire(Geometry geometry, Long idEtude);
  void deleteTerritoireByIdEtude(Long idEtude);
  Territoire createTerritoireFromImportEtude(DataStore dataStore, Long idEtude, TerritoireDto territoireDto);
}

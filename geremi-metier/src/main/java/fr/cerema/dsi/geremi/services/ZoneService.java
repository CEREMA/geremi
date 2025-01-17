package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Map;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.services.dto.ZoneDTO;

/**
 * Interface de service pour la gestion des Zones d'Emploi. Cette interface
 * étend l'interface générique de service pour les entités de type `ZoneEmploi`.
 */
public interface ZoneService extends GenericService<Zone, Long> {

	Zone findZoneByCode(String code, Long id);

	List<Zone> findZonesByCode(Long id);

	void deleteByIdEtude(Long idEtude);

	List<ZoneDTO> findZoneByEtudeInterieur(Long id, double precision);

	List<ZoneDTO> findZoneByEtudeExterieur(Long id, double precision);

	List<String> getZoneNamesByIds(List<Long> ids);

  Map<Long,String> getZoneNamesEtude(Long id);
}

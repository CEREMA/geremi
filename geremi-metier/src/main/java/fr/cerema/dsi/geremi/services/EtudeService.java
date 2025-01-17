package fr.cerema.dsi.geremi.services;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.services.dto.EtudeDTO;
import fr.cerema.dsi.geremi.services.dto.EtudesUtilisateurDTO;
import fr.cerema.dsi.geremi.services.dto.RegionDTO;

public interface EtudeService extends GenericService<Etude, Long> {

  EtudesUtilisateurDTO findEtudeUtilisateur();

  EtudesUtilisateurDTO findEtudeSuiviUtilisateur();

  EtudeDTO save(EtudeDTO etudeDTO);

  EtudeDTO saveEtudeFromImport(EtudeDTO etudeDTO);

  Map<RegionDTO, List<EtudeDTO>> findEtudePublicInRegion();

  Optional<EtudeDTO> getEtudeById(Long id);

  Optional<EtudeDTO> updateEtude(Long id, EtudeDTO etudeDTO);

  Optional<EtudeDTO> publierEtude(EtudeDTO etudeDTO);
}

package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;
import fr.cerema.dsi.geremi.services.dto.RelEtudeUserProcurationDTO;

public interface RelEtudeUserProcurationService extends GenericService<RelEtudeUserProcuration, Long> {
  List<RelEtudeUserProcurationDTO> updateRelByIdEtude(Long idEtude, List<RelEtudeUserProcurationDTO> relEtudeUserProcurationDTO);

  RelEtudeUserProcurationDTO addRelEtudeUserProcuration(RelEtudeUserProcurationDTO relEtudeUserProcurationDTO);

  Optional<RelEtudeUserProcurationDTO> getRelEtudeUserProcurationById(Long id);

}

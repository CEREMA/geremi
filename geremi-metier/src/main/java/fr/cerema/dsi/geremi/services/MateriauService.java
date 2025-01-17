package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Materiau;
import fr.cerema.dsi.geremi.services.dto.MateriauDTO;

public interface MateriauService extends GenericService<Materiau,Integer> {
  Optional<List<MateriauDTO>> getMateriauxDisponibles();

  Optional<List<MateriauDTO>> getMateriauxEtude(Long idEtude);

  Integer supprimerMateriaux(Integer idMateriau);

  MateriauDTO ajoutMateriauEtude(MateriauDTO materiauDTO);

  void deleteByIdEtude(Long idEtude);
}

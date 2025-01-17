package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.ChantierEnvergure;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.dto.ChantierEnvergureDTO;

public interface ChantierEnvergureService extends GenericService<ChantierEnvergure, Integer> {
  Optional<List<Feature>> getChantierEnvergureByIdTerritoire(Long idTerritoire);

  Optional<List<Feature>> getChantierEnvergureByEtude(Long idEtude);

  Optional<List<Feature>> getExistingChantiersByIdTerritoire(Long idTerritoire,Long idEtude);

  Optional<List<Integer>> createChantierEnvergureImport(DataStore dataStore, Long idEtude);

  Optional<Integer> duplicateChantierEnvergureExistante(Integer idChantier, Long idEtude);

  Optional<Integer> deleteChantierEnvergure(Integer idChantier);

  Optional<Integer> modificationChantierEnvergure(ChantierEnvergureDTO chantier);

  Optional<List<Integer>> isInTerritoire(List<Integer> listeChantiers, Long idEtude) throws ImportTypesException;

  Optional<Feature> findByIdChantier(Integer idChantier);

  void deleteByIdEtude(Long idEtude);
}

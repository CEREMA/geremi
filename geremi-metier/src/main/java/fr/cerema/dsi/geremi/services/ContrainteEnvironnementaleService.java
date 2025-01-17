package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;



public interface ContrainteEnvironnementaleService extends GenericService<ContrainteEnvironnementale, Integer> {

	Optional<List<Feature>> getContrainteEnvironnementaleByIdTerritoire(Long idTerritoire);

	Optional<List<Feature>> getContrainteByEtude(Long idEtude);

	Optional<List<Feature>> getExistingContraintesByIdTerritoire(Long idTerritoire);

  Optional<List<Integer>> createContrainteEnvironnementaleImport(DataStore dataStore, Long idEtude);

  Optional<Integer> duplicateContrainteEnvironnementaleExistante(Integer idContrEnv, Long idEtude);

  Optional<Integer> deleteContrainteEnvironnementale(Integer idContrEnv);

  Optional<List<Integer>> isInTerritoire(List<Integer> listeContraintes, Long idEtude) throws ImportTypesException;

  void deleteByIdEtude(Long idEtude);
}

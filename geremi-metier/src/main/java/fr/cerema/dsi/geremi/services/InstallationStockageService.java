package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.InstallationStockage;
import fr.cerema.dsi.geremi.services.dto.InstallationStockageDTO;


public interface InstallationStockageService extends GenericService<InstallationStockage, Integer> {

    Optional<List<Feature>> getInstallationStockageByIdTerritoire(Long idTerritoire);

    Optional<List<Feature>> getInstallationStockageByEtude(Long idEtude);

    Optional<List<Feature>> getExistingInstallationStockageByIdTerritoire(Long idTerritoire, Long idEtude);

    void createInstallationStockageImport(DataStore dataStore, Long idEtude);

    Optional<Integer> duplicateInstallationStockageExistante(Integer idStockage, Long idEtude);

    Optional<Integer> deleteInstallationStockage(Integer idStockage);

    void updateInstallationStockageById(Integer idStockage, InstallationStockageDTO installationStockageDTO);

    Optional<Feature> findFeatureById(Integer idStockage);

  void deleteByIdEtude(Long idEtude);
}

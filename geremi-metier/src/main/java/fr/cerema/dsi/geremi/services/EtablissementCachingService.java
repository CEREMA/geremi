package fr.cerema.dsi.geremi.services;

import java.util.List;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.Etablissement;
import org.wololo.geojson.FeatureCollection;

public interface EtablissementCachingService {

  FeatureCollection findEtablissementsAnneeOriginMatCacheable(String annee, String originMat, Boolean ifPublic);

  FeatureCollection findEtablissementsAnneeEtudeCacheable(String annee, Boolean ifPublic, Long idEtude, Boolean territoireSeul);

}

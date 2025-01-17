package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.entities.Etablissement;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.repositories.DeclarationRepository;
import fr.cerema.dsi.geremi.repositories.EtablissementRepository;
import fr.cerema.dsi.geremi.services.EtablissementCachingService;
import fr.cerema.dsi.geremi.services.EtablissementService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.mapper.EtablissementMapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wololo.geojson.FeatureCollection;

@Service("etablissementCachingService")
public class EtablissementCachingServiceImpl implements EtablissementCachingService {

    private EtablissementRepository etablissementRepository;

    private EtablissementMapper etablissementMapper;


    public EtablissementCachingServiceImpl(EtablissementRepository etablissementRepository, EtablissementMapper etablissementMapper) {
      this.etablissementRepository = etablissementRepository;
      this.etablissementMapper = etablissementMapper;
    }


  @Override
  @Cacheable(value ="ETABLISSEMENT_ORIGINE")
  public FeatureCollection findEtablissementsAnneeOriginMatCacheable(String annee, String originMat, Boolean ifPublic) {
    List<Map <String, Object>> etabs = this.etablissementRepository.findEtablissementAnneeEtOrigineMatEtEtude(annee, originMat, ifPublic, null, null);
    return this.etablissementMapper.jsonToFeatureCollection(etabs);
  }

  @Override
  @Cacheable(value ="ETABLISSEMENT_ETUDE")
  public FeatureCollection findEtablissementsAnneeEtudeCacheable(String annee, Boolean ifPublic, Long idEtude, Boolean territoireSeul) {
    List<Map <String, Object>> etabs = this.etablissementRepository.findEtablissementAnneeEtOrigineMatEtEtude(annee, null, ifPublic, idEtude, territoireSeul);
    return this.etablissementMapper.jsonToFeatureCollection(etabs);
  }

}

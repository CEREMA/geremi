package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.repositories.DeclarationRepository;
import fr.cerema.dsi.geremi.services.EtablissementCachingService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wololo.geojson.FeatureCollection;

import fr.cerema.dsi.geremi.entities.Etablissement;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.repositories.EtablissementRepository;
import fr.cerema.dsi.geremi.services.EtablissementService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.mapper.EtablissementMapper;



@Service("etablissementService")
public class EtablissementServiceImpl implements EtablissementService {


  private EtablissementRepository etablissementRepository;

  private DeclarationRepository declarationRepository;

  private EtablissementMapper etablissementMapper;

  private final SecurityService securityService;

  private final UserService userService;

  private EtablissementCachingService etablissementCachingService;

    public EtablissementServiceImpl(EtablissementRepository etablissementRepository, EtablissementMapper etablissementMapper, SecurityService securityService, UserService userService, DeclarationRepository declarationRepository, EtablissementCachingService etablissementCachingService) {
      this.etablissementRepository = etablissementRepository;
      this.etablissementMapper = etablissementMapper;
      this.securityService = securityService;
      this.userService = userService;
      this.declarationRepository = declarationRepository;
      this.etablissementCachingService = etablissementCachingService;
    }

    @Override
    public List<Etablissement> findAll() {
        return etablissementRepository.findAll();
    }

    @Override
    public Etablissement getOne(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Etablissement findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Etablissement create(Etablissement entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public Etablissement save(Etablissement entity) {
        // TODO Auto-generated method stub
        return null;
    }

  @Override

  public FeatureCollection findEtablissementsAnnee(String annee) {
    return findEtablissementsAnneeEtOrigineMat(annee, null);
  }
  public FeatureCollection findEtablissementsAnneeEtOrigineMat(String annee, String origineMat) {
      boolean ifPublic =  userService.getCurrentUserEntity().map(u -> Role.PUBLIC.getLibelle().equals(u.getProfil().getLibelle())).orElse(true);
      return etablissementCachingService.findEtablissementsAnneeOriginMatCacheable(annee, origineMat, ifPublic);
  }


  public FeatureCollection findEtablissementsAnneeEtude(String annee, Long idEtude, Boolean territoireSeul) {
    securityService.checkConsultationEtude(idEtude);
    boolean ifPublic =  userService.getCurrentUserEntity().map(u -> Role.PUBLIC.getLibelle().equals(u.getProfil().getLibelle())).orElse(true);
    return etablissementCachingService.findEtablissementsAnneeEtudeCacheable(annee, ifPublic, idEtude, territoireSeul);
  }

    @Override
    public List<String> findDistinctAnnees() {
        return declarationRepository.findDistinctAnnees().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}

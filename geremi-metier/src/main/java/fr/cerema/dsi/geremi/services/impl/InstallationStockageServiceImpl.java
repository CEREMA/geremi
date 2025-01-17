package fr.cerema.dsi.geremi.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import fr.cerema.dsi.geremi.services.*;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.Attribut;
import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.InstallationStockage;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.enums.AttributImportControl;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.InstallationStockageRepository;
import fr.cerema.dsi.geremi.repositories.TerritoireRepository;
import fr.cerema.dsi.geremi.services.dto.InstallationStockageDTO;
import fr.cerema.dsi.geremi.services.mapper.InstallationStockageMapper;

import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_DEBUT;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_FIN;

@Service("installationStockageService")
public class InstallationStockageServiceImpl extends GenericServiceImpl<InstallationStockage, Integer>
  implements InstallationStockageService {

  private final InstallationStockageRepository installationStockageRepository;
  private final InstallationStockageMapper installationStockageMapper;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final TerritoireRepository territoireRepository;
  private final TerritoireService territoireService;
  private final SecurityService securityService;
  private final EtudeRepository etudeRepository;

  public InstallationStockageServiceImpl(InstallationStockageRepository installationStockageRepository,
                                         InstallationStockageMapper installationStockageMapper, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService, TerritoireRepository territoireRepository, TerritoireService territoireService, EtudeRepository etudeRepository) {
    this.installationStockageRepository = installationStockageRepository;
    this.installationStockageMapper = installationStockageMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.territoireService = territoireService;
    this.territoireRepository = territoireRepository;
    this.securityService = securityService;
    this.etudeRepository = etudeRepository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getInstallationStockageByIdTerritoire(Long idTerritoire) {
    Optional<Etude> etude = territoireRepository.findById(idTerritoire).map(Territoire::getEtude);
    securityService.checkConsultationEtude(etude);
    List<InstallationStockage> installationStockages = new ArrayList<>();
    List<Integer> idInstallationStockages = this.installationStockageRepository
      .getInstallationStockageByIdTerritoire(idTerritoire).stream()
      .map(InstallationStockage::getIdStockage)
      .toList();

    if (idInstallationStockages.isEmpty()) {
      // Si aucune installation Stockages étude
      installationStockages = this.installationStockageRepository
        .getInstallationStockageByIdTerritoire(idTerritoire);
    } else {
      // Sinon on enlève les installations déjà présente dans l'étude
      installationStockages = this.installationStockageRepository
        .getExistingInstallationStockageByIdTerritoire(idTerritoire,idInstallationStockages);
    }
    if (installationStockages.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.installationStockageMapper
        .installationsToFeature(installationStockages);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getInstallationStockageByEtude(Long idEtude) {
    securityService.checkConsultationEtude(idEtude);
    List<InstallationStockage> installationStockages = this.installationStockageRepository
      .getInstallationByEtude(idEtude);

    if (installationStockages.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.installationStockageMapper.installationsToFeature(installationStockages);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getExistingInstallationStockageByIdTerritoire(Long idTerritoire, Long idEtude) {
    securityService.checkConsultationEtude(idEtude);
    List<InstallationStockage> installationStockages = new ArrayList<>();
    List<Integer> idInstallationEtude = this.installationStockageRepository.getInstallationByEtude(idEtude).stream()
      .map(InstallationStockage::getIdStockage)
      .filter(Objects::nonNull)
      .toList();

    List<InstallationStockage> installationStockagesEtude = this.installationStockageRepository.getInstallationByEtude(idEtude);
    List<Integer> idInstallationsStockage = new ArrayList<>(installationStockagesEtude.stream()
      .map(InstallationStockage::getIdStockage)
      .filter(Objects::nonNull)
      .toList());

    // On récupère la liste des chantiers ayant uniquement un Chantier Frère pour le retirer de la liste
    List<Integer> idInstallationsStockageFrereEtude = installationStockagesEtude.stream()
      .map(InstallationStockage::getIdFrere)
      .filter(Objects::nonNull)
      .toList();
    // On récupère la liste des chantiers ayant uniquement un Chantier Père pour le retirer de la liste
    List<Integer> idInstallationsStockageSourceEtude = installationStockagesEtude.stream()
      .filter(c -> Objects.isNull(c.getIdFrere()))
      .map(InstallationStockage::getInstallationStockageSource)
      .filter(Objects::nonNull)
      .map(InstallationStockage::getIdStockage)
      .toList();

    idInstallationsStockage.addAll(idInstallationsStockageFrereEtude);
    idInstallationsStockage.addAll(idInstallationsStockageSourceEtude);

    if (idInstallationEtude.isEmpty()) {
      installationStockages = this.installationStockageRepository
        .getInstallationStockageByIdTerritoire(idTerritoire);
    } else {
      installationStockages = this.installationStockageRepository
        .getExistingInstallationStockageByIdTerritoire(idTerritoire, idInstallationsStockage);
    }

    if (installationStockages.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.installationStockageMapper.installationsToFeature(installationStockages);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createInstallationStockageImport(DataStore dataStore, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.INSTALLATIONS);
    Etude etude = this.etudeRepository.findById(idEtude).orElse(null);
    dataStore.getElements().values().forEach(stockage -> {
      String nomEtab = null;
      String codeEtab = null;
      String description = null;
      Integer anneeDebut = Objects.isNull(etude) ? null : etude.getAnneeRef();
      Integer anneeFin = null;
      Long betonPref = null;
      Long viabAutre = null;
      Long tonTot = null;
      Geometry theGeom = null;
      for (Attribut attribut : stockage) {
        if (attribut.getName().equals(AttributImportControl.NOM_ETAB.getLibelle())) {
          nomEtab = (String) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.CODE_ETAB.getLibelle())) {
          codeEtab = (String) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.DESCRIP.getLibelle())) {
          description = (String) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.ANNEE_FIN.getLibelle())) {
          anneeFin = Integer.parseInt(attribut.getValue().toString());
        }
        if (attribut.getName().equals(AttributImportControl.BETON_PREF.getLibelle())) {
          betonPref = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.VIAB_AUTRE.getLibelle())) {
          viabAutre = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.TON_TOT.getLibelle())) {
          tonTot = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(AttributImportControl.THEGEOM.getLibelle())) {
          theGeom = (Geometry) attribut.getValue();
          theGeom.setSRID(4326);
        }
      }
      if(Objects.isNull(betonPref) ) {
        betonPref = 0L;
      }
      if(Objects.isNull(viabAutre)) {
        viabAutre = 0L;
      }
      if(Objects.isNull(tonTot)) {
        tonTot = betonPref + viabAutre;
      }

      boolean outsideStudyPeriod =
        (anneeDebut > Objects.requireNonNull(etude).getAnneeFin())
          || (anneeFin < Objects.requireNonNull(etude).getAnneeRef());

      if(this.territoireService.geometryIntersectsTerritoire(theGeom, idEtude) && !outsideStudyPeriod){
        this.installationStockageRepository.createInstallationStockage(nomEtab, codeEtab, description, anneeDebut,
          anneeFin, betonPref, viabAutre, tonTot, theGeom, null, null, idEtude);
      }

      this.tracabiliteEtapeService.addTracabiliteEtape(idEtude,null, Etape.INSTALLATIONS, EtatEtape.IMPORTE);
    });
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> duplicateInstallationStockageExistante(Integer idStockage, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.INSTALLATIONS);
    Optional<InstallationStockage> installationStockage = this.installationStockageRepository.findById(idStockage);
    if(installationStockage.isEmpty()){
      return Optional.empty();
    }
    Integer idSource = null;
    if(Objects.nonNull(installationStockage.get().getInstallationStockageSource())){
      idSource = this.installationStockageRepository.findById(idStockage).get().getInstallationStockageSource().getIdStockage();
    }

    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude,null, Etape.INSTALLATIONS, EtatEtape.MODIFIE);
    if(Objects.isNull(idSource)){
      return Optional.of(this.installationStockageRepository.duplicateInstallationStockageExistante(idStockage, idStockage, null, idEtude));
    }
    return Optional.of(this.installationStockageRepository.duplicateInstallationStockageExistante(idStockage, idSource, idStockage, idEtude));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> deleteInstallationStockage(Integer idStockage) {

    InstallationStockage installationStockage = this.findById(idStockage);
    securityService.checkModificationEtude(Optional.of(installationStockage.getEtude()), null, Etape.INSTALLATIONS);
    Integer idInstallationSource = null;
    if(Objects.nonNull(installationStockage.getInstallationStockageSource())){
      idInstallationSource = installationStockage.getInstallationStockageSource().getIdStockage();
    }
    Long idEtude = installationStockage.getEtude().getId();
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude,null, Etape.INSTALLATIONS, EtatEtape.MODIFIE);

    this.installationStockageRepository.delete(installationStockage);
    if (Objects.isNull(idInstallationSource)) {
      return Optional.of(idStockage);
    }
    if (Objects.nonNull(installationStockage.getIdFrere())) {
      return Optional.of(installationStockage.getIdFrere());
    }
    return Optional.of(idInstallationSource);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateInstallationStockageById(Integer idStockage, InstallationStockageDTO installationStockageDTO) {
    InstallationStockage installationStockage = this.findById(idStockage);
    securityService.checkModificationEtude(Optional.of(installationStockage.getEtude()), null, Etape.INSTALLATIONS);
    Long idEtude = installationStockage.getEtude().getId();


    installationStockage.setNomEtab(installationStockageDTO.getNomEtab());
    installationStockage.setDescription(installationStockageDTO.getDescription());
    installationStockage.setAnneeDebut(installationStockageDTO.getAnneeDebut());
    installationStockage.setAnneeFin(installationStockageDTO.getAnneeFin());
    installationStockage.setBetonPref( installationStockageDTO.getBetonPref());
    installationStockage.setViabAutre(installationStockageDTO.getViabAutre());
    installationStockage.setTonTot(installationStockageDTO.getTonTot());

    this.installationStockageRepository.save(installationStockage);

    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude,null, Etape.INSTALLATIONS, EtatEtape.MODIFIE);
  }


    @Override
    public Optional<Feature> findFeatureById(Integer idStockage) {
        Optional<InstallationStockage> installationStockageOptional = installationStockageRepository.findById(idStockage);

        if (installationStockageOptional.isPresent()) {
          securityService.checkConsultationEtude(Optional.of(installationStockageOptional.get().getEtude()));
            InstallationStockage installationStockage = installationStockageOptional.get();
            Feature feature = installationStockageMapper.toFeature(installationStockage);
            return Optional.of(feature);
        } else {
            return Optional.empty();
        }
    }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.installationStockageRepository.deleteByIdEtude(idEtude);
  }

}

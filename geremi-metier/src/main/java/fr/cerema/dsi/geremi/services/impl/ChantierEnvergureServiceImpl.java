package fr.cerema.dsi.geremi.services.impl;

import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_DEBUT;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_FIN;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.BETON_PREF;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.DESCRIP;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.THEGEOM;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.TON_TOT;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.VIAB_AUTRE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.Attribut;
import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.ChantierEnvergure;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.repositories.ChantierEnvergureRepository;
import fr.cerema.dsi.geremi.services.ChantierEnvergureService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ChantierEnvergureDTO;
import fr.cerema.dsi.geremi.services.mapper.ChantierEnvergureMapper;

@Service("chantierEnvergureService")
public class ChantierEnvergureServiceImpl extends GenericServiceImpl<ChantierEnvergure, Integer> implements ChantierEnvergureService {

  private final ChantierEnvergureRepository chantierEnvergureRepository;
  private final ChantierEnvergureMapper chantierEnvergureMapper;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final EtudeRepository etudeRepository;
  private final SecurityService securityService;

  @Value("${geremi.config.message.erreur.import.chantiers.tous.hors.territoire}")
  String messageErreurHorsTerritoire;


  public ChantierEnvergureServiceImpl(ChantierEnvergureRepository chantierEnvergureRepository, ChantierEnvergureMapper chantierEnvergureMapper, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService, EtudeRepository etudeRepository) {
    this.chantierEnvergureRepository = chantierEnvergureRepository;
    this.chantierEnvergureMapper = chantierEnvergureMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.securityService = securityService;
    this.etudeRepository = etudeRepository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getChantierEnvergureByIdTerritoire(Long idTerritoire) {
    return Optional.empty();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getChantierEnvergureByEtude(Long idEtude) {
    securityService.checkConsultationEtude(idEtude);
    List<ChantierEnvergure> chantierEnvergures = this.chantierEnvergureRepository
      .getChantierByEtude(idEtude);

    if (chantierEnvergures.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.chantierEnvergureMapper.chantiersDTOToFeature(
        chantierEnvergures.stream().map(this.chantierEnvergureMapper::toDto).toList());
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getExistingChantiersByIdTerritoire(Long idTerritoire, Long idEtude) {
    List<ChantierEnvergure> chantierEnvergures;
    securityService.checkConsultationEtude(idEtude);
    List<ChantierEnvergure> chantierEtude = this.chantierEnvergureRepository.getChantierByEtude(idEtude);
    List<Integer> idChantierEtude = new ArrayList<>(chantierEtude.stream()
      .map(ChantierEnvergure::getIdChantier)
      .filter(Objects::nonNull)
      .toList());

    // On récupère la liste des chantiers ayant uniquement un Chantier Frère pour le retirer de la liste
    List<Integer> idChantierFrereEtude = chantierEtude.stream()
      .map(ChantierEnvergure::getIdFrere)
      .filter(Objects::nonNull)
      .toList();
    // On récupère la liste des chantiers ayant uniquement un Chantier Père pour le retirer de la liste
    List<Integer> idChantierSourceEtude = chantierEtude.stream()
      .filter(c -> Objects.isNull(c.getIdFrere()))
      .map(ChantierEnvergure::getChantierSource)
      .filter(Objects::nonNull)
      .map(ChantierEnvergure::getIdChantier)
      .toList();

    idChantierEtude.addAll(idChantierSourceEtude);
    idChantierEtude.addAll(idChantierFrereEtude);

    if(idChantierEtude.isEmpty()){
      // Si aucune Contrainte étude
      chantierEnvergures = this.chantierEnvergureRepository
        .getChantierEnvergureByIdTerritoire(idTerritoire);
    } else {
      // Sinon on enlève les chantiers déjà présente dans l'étude
      chantierEnvergures = this.chantierEnvergureRepository
        .getExistingChantiersByIdTerritoire(idTerritoire,idChantierEtude);
    }

    if (chantierEnvergures.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.chantierEnvergureMapper
        .chantiersToFeature(chantierEnvergures);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<List<Integer>> createChantierEnvergureImport(DataStore dataStore, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.CHANTIERS);
    Etude etude = this.etudeRepository.findById(idEtude).orElse(null);
    List<Integer> listeChantiers = new ArrayList<>();
    dataStore.getElements().values().forEach(chantier -> {
      String nom = null;
      String descrip = null;
      Integer anneeDebut = null;
      Integer anneeFin = null;
      Long betonPref = null;
      Long viabAutre = null;
      Long tonTot = null;
      Geometry geometryZone = null;
      for (Attribut attribut : chantier) {
        if (attribut.getName().equals(NOM.getLibelle())) {
          nom = (String) attribut.getValue();
        }
        if (attribut.getName().equals(DESCRIP.getLibelle())) {
          descrip = (String) attribut.getValue();
        }
        if (attribut.getName().equals(ANNEE_DEBUT.getLibelle())) {
          anneeDebut = (Integer) attribut.getValue();
        }
        if (attribut.getName().equals(ANNEE_FIN.getLibelle())) {
          anneeFin = (Integer) attribut.getValue();
        }
        if (attribut.getName().equals(BETON_PREF.getLibelle()) && Objects.nonNull(attribut.getValue())) {
          betonPref = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(VIAB_AUTRE.getLibelle()) && Objects.nonNull(attribut.getValue())) {
          viabAutre = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(TON_TOT.getLibelle()) && Objects.nonNull(attribut.getValue())) {
          tonTot = (Long) attribut.getValue();
        }
        if (attribut.getName().equals(THEGEOM.getLibelle())) {
          geometryZone = (Geometry) attribut.getValue();
          geometryZone.setSRID(4326);
        }
      }
      if(!(Objects.isNull(betonPref) && Objects.isNull(viabAutre) && !Objects.isNull(tonTot))) {
        if(Objects.isNull(betonPref) ) {
          betonPref = 0L;
        }
        if(Objects.isNull(viabAutre)) {
          viabAutre = 0L;
        }
        if(Objects.isNull(tonTot)) {
          tonTot = betonPref + viabAutre;
        }
      }

      boolean outsideStudyPeriod =
        (anneeDebut > Objects.requireNonNull(etude).getAnneeFin())
        || (anneeFin < Objects.requireNonNull(etude).getAnneeRef());

      if(!outsideStudyPeriod){
        listeChantiers.add(this.chantierEnvergureRepository.createChantierEnvergure(nom, descrip, anneeDebut, anneeFin,
          betonPref, viabAutre, tonTot, geometryZone, null, null, idEtude));
      }
    });

    tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.CHANTIERS, EtatEtape.IMPORTE);
    return Optional.of(listeChantiers);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> duplicateChantierEnvergureExistante(Integer idChantier, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.CHANTIERS);
    Optional<ChantierEnvergure> chantierEnvergure = this.chantierEnvergureRepository.findById(idChantier);
    if(chantierEnvergure.isEmpty()){
      return Optional.empty();
    }
    Integer idSource = null;
    if(Objects.nonNull(chantierEnvergure.get().getChantierSource())){
      idSource = this.chantierEnvergureRepository.findById(idChantier).get().getChantierSource().getIdChantier();
    }
    tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.CHANTIERS, EtatEtape.MODIFIE);
    if(Objects.isNull(idSource)){
      return Optional.of(this.chantierEnvergureRepository.duplicateChantierEnvergureExistante(idChantier, idChantier, null, idEtude));
    }
    return Optional.of(this.chantierEnvergureRepository.duplicateChantierEnvergureExistante(idChantier, idSource, idChantier, idEtude));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> deleteChantierEnvergure(Integer idChantier) {
    ChantierEnvergure chantier = this.findById(idChantier);
    securityService.checkModificationEtude(Optional.of(chantier.getEtude()), null, Etape.CHANTIERS);
    Integer idChantierSource = null;
    if(Objects.nonNull(chantier.getChantierSource())){
      idChantierSource = chantier.getChantierSource().getIdChantier();
    }
    Integer idChantierFrere = chantier.getIdFrere();
    Long idEtude = chantier.getEtude().getId();

    this.chantierEnvergureRepository.deleteChantierEnvergure(idChantier);
    // Si l'idContrainteSource est null, on supprime un chantier mère alors on retourne son id
    tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.CHANTIERS, EtatEtape.MODIFIE);
    // Si l'idContrainteSource est null, on supprime une contrainte mère alors on retourne son id
    if(Objects.isNull(idChantierSource)) {
      return Optional.of(idChantier);
    }
    if(Objects.nonNull(idChantierFrere)){
      return Optional.of(idChantierFrere);
    }
    return Optional.of(idChantierSource);
  }

  @Override
  public Optional<List<Integer>> isInTerritoire(List<Integer> listeChantiers, Long idEtude) throws ImportTypesException {
    securityService.checkConsultationEtude(idEtude);
    List<Integer> chantiersSuppr = new ArrayList<>();
    for(Integer id : listeChantiers){
      if(!this.chantierEnvergureRepository.isInTerritoire(id,idEtude)){
        chantiersSuppr.add(this.deleteChantierEnvergure(id).get());
      }
    }
    if(chantiersSuppr.size() == listeChantiers.size()){
      throw new ImportTypesException(this.messageErreurHorsTerritoire);
    }
    return Optional.of(chantiersSuppr);
  }

  @Override
  public Optional<Feature> findByIdChantier(Integer idChantier) {
    Optional<ChantierEnvergure> chantierEnvergure = this.chantierEnvergureRepository.findById(idChantier);
    if(chantierEnvergure.isPresent()){
      securityService.checkConsultationEtude(Optional.of(chantierEnvergure.get().getEtude()));
      return chantierEnvergure.stream().map(this.chantierEnvergureMapper::toFeature).findFirst();
    }
    return Optional.empty();
  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.chantierEnvergureRepository.deleteByIdEtude(idEtude);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> modificationChantierEnvergure(ChantierEnvergureDTO chantier) {
    ChantierEnvergure newChantier = this.findById(chantier.getIdChantier());
    securityService.checkModificationEtude(Optional.of(newChantier.getEtude()), null, Etape.CHANTIERS);
    if(Objects.nonNull(chantier.getNom())){
      newChantier.setNom(chantier.getNom());
    }
    newChantier.setDescription(chantier.getDescription());
    newChantier.setAnneeDebut(chantier.getAnneeDebut());
    newChantier.setAnneeFin(chantier.getAnneeFin());
    newChantier.setBetonPref(chantier.getBetonPref());
    newChantier.setViabAutre(chantier.getViabAutre());
    newChantier.setTonTot(chantier.getTonTot());
    tracabiliteEtapeService.addTracabiliteEtape(newChantier.getEtude().getId(), null, Etape.CHANTIERS, EtatEtape.MODIFIE);
    return Optional.ofNullable(this.save(newChantier).getIdChantier());
  }
}

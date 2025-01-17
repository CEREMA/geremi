package fr.cerema.dsi.geremi.services.impl;

import static fr.cerema.dsi.geremi.enums.AttributImportControl.CODE_ZONE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.DESCRIP;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM_ZONE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.THEGEOM;
import static fr.cerema.dsi.geremi.enums.TypeTerritoire.BASSIN_VIE;
import static fr.cerema.dsi.geremi.enums.TypeTerritoire.COMMUNE;
import static fr.cerema.dsi.geremi.enums.TypeTerritoire.DEPARTEMENT;
import static fr.cerema.dsi.geremi.enums.TypeTerritoire.EPCI;
import static fr.cerema.dsi.geremi.enums.TypeTerritoire.ZONE_EMPLOI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.geremi.services.mapper.ZoneMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.datastore.Attribut;
import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.enums.TypeTerritoire;
import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import fr.cerema.dsi.geremi.repositories.TerritoireRepository;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.TauxCouvertureDTO;
import fr.cerema.dsi.geremi.services.dto.TerritoireDto;
import fr.cerema.dsi.geremi.services.dto.ZoneDTO;
import fr.cerema.dsi.geremi.services.errors.TauxHorsTerritoireException;

@Service("territoireService")
public class TerritoireServiceImpl extends GenericServiceImpl<Territoire, Long> implements TerritoireService {

  private final Logger logger = LoggerFactory.getLogger(TerritoireServiceImpl.class);

  @Value("${geremi.config.territoire.taux.horsfrance.accepte}")
  private Double horsFranceAccepte;
  @Value("${geremi.config.territoire.taux.horsregion.accepte}")
  private Double horsRegionAccepte;
  @Value("${geremi.config.territoire.taux.noncouvert.accepte}")
  private Double nonCouvertAccepte;



  private final TerritoireRepository territoireRepository;
  private final ZoneRepository zoneRepository;
  private final EtudeRepository etudeRepository;

  private final SecurityService securityService;

  private final TracabiliteEtapeService tracabiliteEtapeService;

  private final ZoneMapper zoneMapper;

  public TerritoireServiceImpl(TerritoireRepository territoireRepository, ZoneRepository zoneRepository, EtudeRepository etudeRepository, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService, ZoneMapper zoneMapper) {
    this.territoireRepository = territoireRepository;
    this.zoneRepository = zoneRepository;
    this.etudeRepository = etudeRepository;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.securityService = securityService;
    this.zoneMapper = zoneMapper;
  }

  @Override
  public Territoire create(Territoire entity) {
    securityService.checkModificationEtude(Optional.of(entity.getEtude()),null, Etape.ZONAGE);
    return this.territoireRepository.save(entity);
  }

  @Override
  public List<Zone> getZoneForEtude(Long id,String precision) {
    securityService.checkConsultationEtude(id);
    return this.zoneRepository.findByEtude(id,Double.parseDouble(precision));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public List<ZoneDTO> findZoneByEtude(Long id) {
    securityService.checkConsultationEtude(id);
    return  this.zoneRepository.findZoneByEtude(id).stream()
      .map(this.zoneMapper::toDto)
      .toList();
  }

  @Override
  public Territoire findTerritoireByEtude(Etude etude) {
    securityService.checkConsultationEtude(Optional.of(this.etudeRepository.getOne(etude.getId())));
    return this.territoireRepository.findByEtude(etude);
  }

  @Override
  public Optional<String> findTypeZonageByIdEtude(Etude etude) {
    securityService.checkConsultationEtude(Optional.of(etude));
    return this.territoireRepository.findTypeZonageByIdEtude(etude.getId());
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = TauxHorsTerritoireException.class)
  public void createTerritoireFromImport(DataStore dataStore, Long idEtude) {
    securityService.checkModificationEtude(idEtude,null, Etape.ZONAGE);
    this.zoneRepository.deleteAllFromEtude(idEtude);
    this.territoireRepository.deleteByIdEtude(idEtude);
    logger.debug("Insertion des zones en base begin");
    dataStore.getElements().values().forEach(zone -> {
      String codeZone = null;
      String nomZone = null;
      String descriptionZone = null;
      Geometry geometryZone = null;
      for(Attribut attribut : zone){
        if(attribut.getName().equals(CODE_ZONE.getLibelle())){
          codeZone = (String)attribut.getValue();
        }
        if(attribut.getName().equals(NOM_ZONE.getLibelle())){
          nomZone = (String) attribut.getValue();
        }
        if(attribut.getName().equals(DESCRIP.getLibelle())){
          descriptionZone = (String) attribut.getValue();
        }
        if(attribut.getName().equals(THEGEOM.getLibelle())){
          geometryZone = (Geometry)attribut.getValue();
          geometryZone.setSRID(4326);
        }
      }
      this.zoneRepository.createZonePerso("perso",nomZone,descriptionZone,idEtude,geometryZone,codeZone);
    });

    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.ZONAGE, EtatEtape.IMPORTE);
  }
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = TauxHorsTerritoireException.class)
  public TauxCouvertureDTO calculTauxCouverture(Long idEtude) throws TauxHorsTerritoireException {
    securityService.checkConsultationEtude(idEtude);
    logger.debug("Insertion des zones en base begin");
    logger.debug("Creation du territoire + calcul begin");
    Long idTerritoire = this.territoireRepository.createTerritoireFromImport("perso", "tmp_perso", "tmp_perso", idEtude);
    logger.debug("Creation du territoire + calcul end");

    TauxCouvertureDTO tauxCouvertureDTO = new TauxCouvertureDTO();
    Etude etude = this.etudeRepository.getOne(idEtude);

    if(etude.isIfSrc()){
      Long idRegion = etude.getUser().getRefRegion().getId();
      TauxCouvertureDTO tmp2 = this.findTauxCouvertureRegion(idEtude, idRegion);
      tauxCouvertureDTO.setTauxNonCouvertCalcule(tmp2.getTauxNonCouvertCalcule());
      tauxCouvertureDTO.setTauxNonCouvertAccepte(tmp2.getTauxNonCouvertAccepte());

      TauxCouvertureDTO tmp1 = this.findTauxTerritoireHorsRegion(idEtude, idRegion);
      tauxCouvertureDTO.setTauxHorsCouvertureCalcule(tmp1.getTauxHorsCouvertureCalcule());
      tauxCouvertureDTO.setTauxHorsCouvertureAccepte(tmp1.getTauxHorsCouvertureAccepte());
    }
    else {
      tauxCouvertureDTO = this.findTauxTerritoireHorsFrance(idEtude);
    }
    tauxCouvertureDTO.setIdTerritoire(idTerritoire);

    return tauxCouvertureDTO;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public boolean geometryIntersectsTerritoire(Geometry geometry, Long idEtude) {
    geometry.setSRID(4326);
    securityService.checkConsultationEtude(idEtude);
    return this.territoireRepository.intersectsTerritoire(geometry,idEtude);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public boolean geometryIsInTerritoire(Geometry geometry, Long idEtude) {
    geometry.setSRID(4326);
    securityService.checkConsultationEtude(idEtude);
    return this.territoireRepository.isInTerritoire(geometry,idEtude);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Territoire validationCreationTerritoire(TerritoireDto territoireDto) {
    Etude etude = this.etudeRepository.getOne(territoireDto.getIdEtude());
    securityService.checkModificationEtude(Optional.of(etude),null, Etape.ZONAGE);
    if(territoireDto.isValideEtape()) {
      Territoire territoire = this.territoireRepository.findByEtude(etude);
      territoire.setNom(territoireDto.getNom());
      territoire.setDescription(territoireDto.getDescription());
      return this.territoireRepository.save(territoire);
    }

    List<Zone> zones = this.zoneRepository.findByEtude(territoireDto.getIdEtude(), 0);
    if(etude.isIfSrc()){
      Long idRegion = etude.getUser().getRefRegion().getId();
      for(Zone z : zones){
        this.zoneRepository.updateTerritoireInRegion(z.getId(), idRegion);
      }
      this.territoireRepository.updateTerritoireInRegion(territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getIdEtude());
    } else {
      for(Zone z : zones){
        this.zoneRepository.updateTerritoireInFrance(z.getId());
      }
      this.territoireRepository.updateTerritoireInFrance(territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getIdEtude());
    }

    this.zoneRepository.cleanZoneVideIdEtude(territoireDto.getIdEtude());

    this.tracabiliteEtapeService.addTracabiliteEtape(territoireDto.getIdEtude(), null, Etape.ZONAGE, EtatEtape.VALIDE);

    return this.territoireRepository.findByEtude(this.etudeRepository.getOne(territoireDto.getIdEtude()));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Territoire createTerritoire(TerritoireDto territoireDto) {
    Etude etude = this.etudeRepository.getOne(territoireDto.getIdEtude());
    securityService.checkModificationEtude(Optional.of(etude),null, Etape.ZONAGE);
    if(territoireDto.isValideEtape()) {
      Territoire territoire = this.territoireRepository.findByEtude(etude);
      territoire.setNom(territoireDto.getNom());
      territoire.setDescription(territoireDto.getDescription());
      return this.territoireRepository.save(territoire);
    }

    this.zoneRepository.deleteAllFromEtude(territoireDto.getIdEtude());
    this.territoireRepository.deleteByIdEtude(territoireDto.getIdEtude());

    /* Création du territoire */
    if (TypeTerritoire.REGION.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireRegion(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }
    if (DEPARTEMENT.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireDepartement(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }
    if (TypeTerritoire.ZONE_EMPLOI.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireZoneEmploi(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }
    if (TypeTerritoire.BASSIN_VIE.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireBassinVie(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }
    if (TypeTerritoire.EPCI.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireEPCI(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }
    if (TypeTerritoire.COMMUNE.type().equals(territoireDto.getType())) {
      this.territoireRepository.createTerritoireCommune(territoireDto.getType(), territoireDto.getNom(), territoireDto.getDescription(), territoireDto.getListeTerritoire(), territoireDto.getIdEtude(), territoireDto.getListeId());
    }

    Territoire territoireSaved = this.territoireRepository.findByEtude(this.etudeRepository.getOne(territoireDto.getIdEtude()));
    ZoneDTO zoneDTO = territoireDto.getZoneDTO();
    zoneDTO.setIdTerritoire(territoireSaved.getId());
    if(DEPARTEMENT.type().equals(zoneDTO.getType())){
      this.zoneRepository.createZoneDepartement(zoneDTO.getType(), zoneDTO.getNom(), zoneDTO.getIdEtude(), territoireDto.getListeId());
    }
    if(ZONE_EMPLOI.type().equals(zoneDTO.getType())){
      this.zoneRepository.createZoneZoneEmploi(zoneDTO.getType(), zoneDTO.getNom(), zoneDTO.getIdEtude(), zoneDTO.getIdTerritoire());
    }
    if(BASSIN_VIE.type().equals(zoneDTO.getType())){
      this.zoneRepository.createZoneBassinVie(zoneDTO.getType(), zoneDTO.getNom(), zoneDTO.getIdEtude(), zoneDTO.getIdTerritoire());
    }
    if(EPCI.type().equals(zoneDTO.getType())){
      this.zoneRepository.createZoneEPCI(zoneDTO.getType(), zoneDTO.getNom(), zoneDTO.getIdEtude(), zoneDTO.getIdTerritoire());
    }
    if(COMMUNE.type().equals(zoneDTO.getType())){
      this.zoneRepository.createZoneCommune(zoneDTO.getType(), zoneDTO.getNom(), zoneDTO.getIdEtude(), zoneDTO.getIdTerritoire());
    }

    this.zoneRepository.cleanZoneVideIdEtude(territoireDto.getIdEtude());

    this.tracabiliteEtapeService.addTracabiliteEtape(territoireDto.getIdEtude(), null, Etape.ZONAGE, EtatEtape.VALIDE);

    return territoireSaved;
  }

  @Override
  public List<Territoire> findAll() {
    throw new NotImplementedException();
  }

  @Override
  public Territoire findById(Long id) {
    Optional<Territoire> territoire = this.territoireRepository.findById(id);
    securityService.checkConsultationEtude(territoire.map(Territoire::getEtude));
    return territoire.orElse(null);
  }

  @Override
  public Territoire findByIdWithPrecision(Long id, String precision) {
    Territoire territoire = this.territoireRepository.findByIdWithPrecision(id, Double.parseDouble(precision));
    securityService.checkConsultationEtude(Optional.of(territoire.getEtude()));
    return territoire;
  }

  @Override
  public void deleteById(Long id) {
    Optional<Territoire> territoire = this.territoireRepository.findById(id);
    securityService.checkModificationEtude(territoire.map(Territoire::getEtude), null, Etape.ZONAGE);
    territoire.ifPresent(territoireRepository::delete);
  }

  @Override
  public Territoire save(Territoire entity) {
    securityService.checkModificationEtude(Optional.of(entity.getEtude()), null, Etape.ZONAGE);
    return this.territoireRepository.save(entity);
  }

  @Override
  public TauxCouvertureDTO findTauxTerritoireHorsFrance(Long idEtude) throws TauxHorsTerritoireException {
    securityService.checkConsultationEtude(idEtude);
    TauxCouvertureDTO tauxCouvertureDTO = new TauxCouvertureDTO();
    tauxCouvertureDTO.setTauxHorsCouvertureAccepte(this.horsFranceAccepte);
    tauxCouvertureDTO.setTauxHorsCouvertureCalcule(this.zoneRepository.findTauxZoneHorsFrance(idEtude));

    if(tauxCouvertureDTO.getTauxHorsCouvertureCalcule() > tauxCouvertureDTO.getTauxHorsCouvertureAccepte()){
      // On arrondi pour l'affichage
      BigDecimal calculerArrondi = new BigDecimal(String.valueOf(tauxCouvertureDTO.getTauxHorsCouvertureCalcule())).setScale(4, RoundingMode.FLOOR);
      String message = "Le territoire importé sort des limites du territoire français ( taux erreur : "+ calculerArrondi +"% > "+tauxCouvertureDTO.getTauxHorsCouvertureAccepte()+"%)";
      throw new TauxHorsTerritoireException(message);
    }
    return tauxCouvertureDTO;
  }

  @Override
  public TauxCouvertureDTO findTauxTerritoireHorsRegion(Long idEtude, Long idRegion) throws TauxHorsTerritoireException {
    securityService.checkConsultationEtude(idEtude);
    TauxCouvertureDTO tauxCouvertureDTO = new TauxCouvertureDTO();
    tauxCouvertureDTO.setTauxHorsCouvertureAccepte(this.horsRegionAccepte);
    tauxCouvertureDTO.setTauxHorsCouvertureCalcule(this.zoneRepository.findTauxZoneHorsRegion(idEtude,idRegion));

    if(tauxCouvertureDTO.getTauxHorsCouvertureCalcule() > tauxCouvertureDTO.getTauxHorsCouvertureAccepte()){
      // On arrondi pour l'affichage
      BigDecimal calculerArrondi = new BigDecimal(String.valueOf(tauxCouvertureDTO.getTauxHorsCouvertureCalcule())).setScale(4, RoundingMode.FLOOR);
      String message = "Le territoire importé sort des limites de la région ( taux erreur : "+ calculerArrondi +"% > "+tauxCouvertureDTO.getTauxHorsCouvertureAccepte()+"%)";
      throw new TauxHorsTerritoireException(message);
    }
    return tauxCouvertureDTO;
  }

  @Override
  public TauxCouvertureDTO findTauxCouvertureRegion(Long idEtude, Long idRegion) throws TauxHorsTerritoireException {
    securityService.checkConsultationEtude(idEtude);
    TauxCouvertureDTO tauxCouvertureDTO = new TauxCouvertureDTO();
    tauxCouvertureDTO.setTauxNonCouvertAccepte(this.nonCouvertAccepte);
    Double calculeCouvert = this.zoneRepository.findTauxCouvertureRegion(idEtude,idRegion);
    if(calculeCouvert > 100d){
      tauxCouvertureDTO.setTauxNonCouvertCalcule(0d);
    } else {
      tauxCouvertureDTO.setTauxNonCouvertCalcule(100 - calculeCouvert);
    }
    if(tauxCouvertureDTO.getTauxNonCouvertCalcule() > tauxCouvertureDTO.getTauxNonCouvertAccepte()){
      // On arrondi pour l'affichage
      BigDecimal calculerArrondi = new BigDecimal(String.valueOf(tauxCouvertureDTO.getTauxNonCouvertCalcule())).setScale(4, RoundingMode.FLOOR);
      String message = "Le territoire importé ne couvre pas le territoire de la region ( taux erreur : "+ calculerArrondi +"% > "+tauxCouvertureDTO.getTauxNonCouvertAccepte()+"%)";
      throw new TauxHorsTerritoireException(message);
    }
    return tauxCouvertureDTO;
  }

  @Override
  public void deleteTerritoireByIdEtude(Long idEtude) {
    this.zoneRepository.deleteAllFromEtude(idEtude);
    this.territoireRepository.deleteByIdEtude(idEtude);

    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.ZONAGE, EtatEtape.NON_RENSEIGNE);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Territoire createTerritoireFromImportEtude(DataStore dataStore, Long idEtude, TerritoireDto territoireDto) {
    this.zoneRepository.deleteAllFromEtude(idEtude);
    this.territoireRepository.deleteByIdEtude(idEtude);
    logger.debug("Insertion des zones en base begin");
    dataStore.getElements().values().forEach(zone -> {
      String codeZone = null;
      String nomZone = null;
      String descriptionZone = null;
      Geometry geometryZone = null;
      for(Attribut attribut : zone){
        if(attribut.getName().equals(CODE_ZONE.getLibelle())){
          codeZone = (String)attribut.getValue();
        }
        if(attribut.getName().equals(NOM_ZONE.getLibelle())){
          nomZone = (String) attribut.getValue();
        }
        if(attribut.getName().equals(DESCRIP.getLibelle())){
          descriptionZone = (String) attribut.getValue();
        }
        if(attribut.getName().equals(THEGEOM.getLibelle())){
          geometryZone = (Geometry)attribut.getValue();
          geometryZone.setSRID(4326);
        }
      }
      this.zoneRepository.createZonePerso("perso",nomZone,descriptionZone,idEtude,geometryZone,codeZone);
    });

    this.territoireRepository.createTerritoireFromImport("perso",territoireDto.getNom(), territoireDto.getDescription(), idEtude);

    return this.territoireRepository.findByEtude_Id(idEtude);
  }
}

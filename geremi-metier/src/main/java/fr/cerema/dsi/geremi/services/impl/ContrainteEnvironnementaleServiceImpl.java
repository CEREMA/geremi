package fr.cerema.dsi.geremi.services.impl;

import static fr.cerema.dsi.geremi.enums.AttributImportControl.DESCRIP;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NIVEAU;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.THEGEOM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;

import fr.cerema.dsi.commons.datastore.Attribut;
import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.repositories.ContrainteEnvironnementaleRepository;
import fr.cerema.dsi.geremi.services.ContrainteEnvironnementaleService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.mapper.ContrainteEnvironnementaleMapper;

@Service("contrainteEnvironnementaleService")
public class ContrainteEnvironnementaleServiceImpl extends GenericServiceImpl<ContrainteEnvironnementale, Integer>
  implements ContrainteEnvironnementaleService {

  private final ContrainteEnvironnementaleRepository contrainteEnvironnementaleRepository;
  private final ContrainteEnvironnementaleMapper contrainteEnvironnementaleMapper;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final TerritoireService territoireService;
  private final SecurityService securityService;

  @Value("${geremi.config.message.erreur.import.contraintes.toutes.hors.territoire}")
  String messageErreurHorsTerritoire;

  public ContrainteEnvironnementaleServiceImpl(
    ContrainteEnvironnementaleRepository contrainteEnvironnementaleRepository,
    ContrainteEnvironnementaleMapper contrainteEnvironnementaleMapper,
    TracabiliteEtapeService tracabiliteEtapeService,
    TerritoireService territoireService,
    SecurityService securityService) {
    this.contrainteEnvironnementaleRepository = contrainteEnvironnementaleRepository;
    this.contrainteEnvironnementaleMapper = contrainteEnvironnementaleMapper;
		this.tracabiliteEtapeService = tracabiliteEtapeService;
		this.territoireService = territoireService;
		this.securityService = securityService;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getContrainteEnvironnementaleByIdTerritoire(Long idTerritoire) {
    Territoire territoire = territoireService.findById(idTerritoire);
    securityService.checkConsultationEtude(Optional.of(territoire.getEtude()));
    List<ContrainteEnvironnementale> contrainteEnvironnementales = this.contrainteEnvironnementaleRepository
      .getContrainteEnvironnementaleByIdTerritoire(idTerritoire);

    if (contrainteEnvironnementales.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.contrainteEnvironnementaleMapper
        .contraintesToFeature(contrainteEnvironnementales);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<List<Feature>> getContrainteByEtude(Long idEtude) {
    securityService.checkConsultationEtude(idEtude);
    List<ContrainteEnvironnementale> contrainteEnvironnementales = this.contrainteEnvironnementaleRepository
      .getContrainteByEtude(idEtude);

    if (contrainteEnvironnementales.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.contrainteEnvironnementaleMapper
        .contraintesToFeature(contrainteEnvironnementales);
      return Optional.of(features);
    }
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<List<Feature>> getExistingContraintesByIdTerritoire(Long idTerritoire) {
    Territoire territoire = territoireService.findById(idTerritoire);
    securityService.checkConsultationEtude(Optional.of(territoire.getEtude()));
    List<ContrainteEnvironnementale> contrainteEnvironnementales;
    List<Integer> idContrainteSourceEtude = this.contrainteEnvironnementaleRepository.getContrainteByEtude(territoire.getEtude().getId()).stream()
      .map(ContrainteEnvironnementale::getIdSource)
      .filter(Objects::nonNull)
      .toList();

    if(idContrainteSourceEtude.isEmpty()){
      // Si aucune Contrainte étude
      contrainteEnvironnementales = this.contrainteEnvironnementaleRepository
        .getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
    } else {
      // Sinon on enlève les contraintes déjà présente dans l'étude
      contrainteEnvironnementales = this.contrainteEnvironnementaleRepository
        .getExistingContraintesByIdTerritoire(idTerritoire,idContrainteSourceEtude);
    }

    if (contrainteEnvironnementales.isEmpty()) {
      return Optional.empty();
    } else {
      List<Feature> features = this.contrainteEnvironnementaleMapper
        .contraintesToFeature(contrainteEnvironnementales);
      return Optional.of(features);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
	public Optional<Integer> duplicateContrainteEnvironnementaleExistante(Integer idContrEnv, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.CONTRAINTES);
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, null,Etape.CONTRAINTES,  EtatEtape.MODIFIE);
    Integer res = this.contrainteEnvironnementaleRepository.duplicateContrainteEnvironnementaleExistante(idContrEnv, idEtude);
		return Optional.of(res);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<Integer> deleteContrainteEnvironnementale(Integer idContrEnv) {
    ContrainteEnvironnementale contrainte = this.findById(idContrEnv);
    Long idEtude = contrainte.getEtude().getId();
    Integer idContrainteSource = contrainte.getIdSource();

    securityService.checkModificationEtude(Optional.of(contrainte.getEtude()), null, Etape.CONTRAINTES);

    this.contrainteEnvironnementaleRepository.deleteContrainteEnvironnementale(idContrEnv);
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.CONTRAINTES, EtatEtape.MODIFIE);
    // Si l'idContrainteSource est null, on supprime une contrainte mère alors on retourne son id
    if(Objects.isNull(idContrainteSource)){
      return Optional.of(idContrEnv);
    }
    return Optional.of(idContrainteSource);
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<List<Integer>> createContrainteEnvironnementaleImport(DataStore dataStore, Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.CONTRAINTES);
    List<Integer> listeContraintes = new ArrayList<>();
    dataStore.getElements().values().forEach(zone -> {
      String nom = null;
      String descrip = null;
      String niveau = null;
      Geometry geometryZone = null;
      for (Attribut attribut : zone) {
        if (attribut.getName().equals(NOM.getLibelle())) {
          nom = (String) attribut.getValue();
        }
        if (attribut.getName().equals(DESCRIP.getLibelle())) {
          descrip = (String) attribut.getValue();
        }
        if (attribut.getName().equals(NIVEAU.getLibelle())) {
          niveau = (String) attribut.getValue();
          niveau = niveau.substring(0, 1).toUpperCase() + niveau.substring(1).toLowerCase();
        }
        if (attribut.getName().equals(THEGEOM.getLibelle())) {
          geometryZone = (Geometry) attribut.getValue();
          geometryZone.setSRID(4326);
        }
      }
      listeContraintes.add(this.contrainteEnvironnementaleRepository.createContrainteEnvironnementale(nom, descrip, niveau,
        geometryZone, null, idEtude));
    });
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, null, Etape.CONTRAINTES, EtatEtape.IMPORTE);
    return Optional.of(listeContraintes);
  }

  @Override
  public Optional<List<Integer>> isInTerritoire(List<Integer> listeContraintes, Long idEtude) throws ImportTypesException {
    securityService.checkConsultationEtude(idEtude);
    List<Integer> contraintesSuppr = new ArrayList<>();
    for(Integer id : listeContraintes){
      if(!this.contrainteEnvironnementaleRepository.isInTerritoire(id,idEtude)){
        contraintesSuppr.add(this.deleteContrainteEnvironnementale(id).get());
      }
    }
    if(contraintesSuppr.size() == listeContraintes.size()){
      throw new ImportTypesException(this.messageErreurHorsTerritoire);
    }

    return Optional.of(contraintesSuppr);
  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.contrainteEnvironnementaleRepository.deleteByIdEtude(idEtude);
  }
}

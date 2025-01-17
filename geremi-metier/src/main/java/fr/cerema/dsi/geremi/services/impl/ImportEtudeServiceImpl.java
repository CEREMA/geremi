package fr.cerema.dsi.geremi.services.impl;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.datastore.entities.Resultats;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.services.*;
import fr.cerema.dsi.geremi.services.dto.*;
import fr.cerema.dsi.geremi.services.mapper.UserMapper;
import fr.cerema.dsi.geremi.services.mapper.ZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service("importEtudeService")
public class ImportEtudeServiceImpl implements ImportEtudeService {

  @Autowired
  private TerritoireService territoireService;

  @Autowired
  private ScenarioService scenarioService;

  @Autowired
  private EtudeService etudeService;

  @Autowired
  private RelResultatZoneService relResultatZoneService;

  @Autowired
  private ResultatCalculService resultatCalculService;

  @Autowired
  private TracabiliteEtapeService tracabiliteEtapeService;

  @Autowired
  private ZoneMapper zoneMapper;


  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Optional<ScenarioDTO> importEtudeFromFiles(ImportEtudeDTO importEtudeDTO,
                                                    DataStore dataStore,
                                                    List<Resultats> resultats) {
    EtudeDTO newEtude = this.etudeService.saveEtudeFromImport(importEtudeDTO.getEtude());
    importEtudeDTO.getEtude().setId(newEtude.getId());

    ScenarioDTO scenarioDTO = importEtudeDTO.getScenario();
    scenarioDTO.setEtudeDTO(importEtudeDTO.getEtude());
    scenarioDTO.setDateMaj(LocalDateTime.now());
    scenarioDTO.setIfRetenu(true);

    Optional<ScenarioDTO> scenarioOptional =
      this.scenarioService.createScenarioFromImport(scenarioDTO);

    scenarioDTO.setId(scenarioOptional.get().getId());

    Territoire territoire = this.territoireService.createTerritoireFromImportEtude(dataStore,newEtude.getId(), importEtudeDTO.getEtude().getTerritoire());

    Map<String,ZoneDTO> mapZone = new HashMap<>();
    Map<Integer,List<RelResultatZone>> mapRelZone = new HashMap<>();

    this.territoireService.getZoneForEtude(newEtude.getId(),"0").stream()
      .map(this.zoneMapper::toDto)
      .forEach(zone -> mapZone.put(zone.getCode(), zone));

    for (Resultats resultat : resultats) {
      ZoneDTO zoneDTO = mapZone.get(resultat.getCodeZone());

      RelResultatZone relZone = new RelResultatZone();
      relZone.setIdZone(zoneDTO.getIdZone());
      relZone.setProductionZonePrimaire(resultat.getProdprim());
      relZone.setProductionZoneTotal(resultat.getProdtot());
      relZone.setProductionZoneSecondaire(resultat.getProdtot() - resultat.getProdprim());
      relZone.setProductionZonePrimaireBrut(resultat.getPprimbrute());
      relZone.setProductionZonePrimaireIntra(resultat.getPprimintra());
      if (resultat.getBeschant() == null) {
        resultat.setBeschant(0.0);
      }
      relZone.setBesoinZoneTotalChantier(resultat.getBeschant());
      relZone.setBesoinZonePrimaire(resultat.getBesprim());
      relZone.setBesoinZoneTotal(resultat.getBestot());
      if (resultat.getBesprim() != null) {
        relZone.setBesoinZoneSecondaire(resultat.getBestot()-resultat.getBesprim()-resultat.getBeschant());
      }
      List<RelResultatZone> relResultatZones =
        Objects.isNull(mapRelZone.get(resultat.getAnnee())) ?
          new ArrayList<>() : mapRelZone.get(resultat.getAnnee());
      relResultatZones.add(relZone);

      mapRelZone.put(resultat.getAnnee(),relResultatZones);
    }

    for(Integer annee : mapRelZone.keySet()){
      List<RelResultatZone> relResultatZones = mapRelZone.get(annee);

      ResultatCalcul resultatCalcul = new ResultatCalcul();
      resultatCalcul.setIdScenario(scenarioDTO.getId());
      resultatCalcul.setIdTerritoire(territoire.getId());
      resultatCalcul.setDateMaj(LocalDateTime.now());
      resultatCalcul.setIfProjection(true);
      resultatCalcul.setAnnee(annee);

      BigDecimal prodTerPrim = BigDecimal.ZERO;
      BigDecimal prodTerBrut = BigDecimal.ZERO;
      BigDecimal prodTerIntra = BigDecimal.ZERO;
      BigDecimal prodTerTot = BigDecimal.ZERO;

      BigDecimal besTerChant = BigDecimal.ZERO;
      BigDecimal besTerPrim = BigDecimal.ZERO;
      BigDecimal besTerTot = BigDecimal.ZERO;

      for(RelResultatZone rel : relResultatZones){
        prodTerPrim = rel.getProductionZonePrimaire() != null ? prodTerPrim.add(BigDecimal.valueOf(rel.getProductionZonePrimaire())) : prodTerPrim;
        prodTerBrut = rel.getProductionZonePrimaireBrut() != null ? prodTerBrut.add(BigDecimal.valueOf(rel.getProductionZonePrimaireBrut())) : prodTerBrut;
        prodTerIntra = rel.getProductionZonePrimaireIntra() != null ? prodTerIntra.add(BigDecimal.valueOf(rel.getProductionZonePrimaireIntra())) : prodTerIntra;
        prodTerTot = rel.getProductionZoneTotal() != null ? prodTerTot.add(BigDecimal.valueOf(rel.getProductionZoneTotal())) : prodTerTot;

        besTerChant = rel.getBesoinZoneTotalChantier() != null ? besTerChant.add(BigDecimal.valueOf(rel.getBesoinZoneTotalChantier())) : besTerChant;
        besTerPrim = rel.getBesoinZonePrimaire() != null ? besTerPrim.add(BigDecimal.valueOf(rel.getBesoinZonePrimaire())) : besTerPrim;
        besTerTot = rel.getBesoinZoneTotal() != null ? besTerTot.add(BigDecimal.valueOf(rel.getBesoinZoneTotal())) : besTerTot;
      }

      resultatCalcul.setProductionTerritoirePrimaire(prodTerPrim.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoirePrimaireBrut(prodTerBrut.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoirePrimaireIntra(prodTerIntra.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoireTotal(prodTerTot.setScale(3, RoundingMode.HALF_UP).doubleValue());

      resultatCalcul.setBesoinTerritoireTotalChantier(besTerChant.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setBesoinTerritoirePrimaire(besTerPrim.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setBesoinTerritoireTotal(besTerTot.setScale(3, RoundingMode.HALF_UP).doubleValue());

      resultatCalcul.setPourcentProductionTerritoireSecondaire(0d);

      resultatCalcul = this.resultatCalculService.save(resultatCalcul);

      for(RelResultatZone rel : relResultatZones){
        rel.setIdResultat(resultatCalcul.getId());
        rel.setDateMaj(LocalDateTime.now());
        this.relResultatZoneService.create(rel);
      }
    }

    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.ZONAGE, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.POPULATION, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.CONTRAINTES, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.CHANTIERS, EtatEtape.VALIDE_VIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.INSTALLATIONS, EtatEtape.VALIDE_VIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), null, Etape.MATERIAUX, EtatEtape.VALIDE_VIDE);

    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), scenarioDTO.getId(), Etape.CREATION_SCENARIO, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), scenarioDTO.getId(), Etape.CONTRAINTES_SCENARIO, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), scenarioDTO.getId(), Etape.HYPOTHESE_VENTILATION_SCENARIO, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(newEtude.getId(), scenarioDTO.getId(), Etape.HYPOTHESE_PROJECTION_SCENARIO, EtatEtape.VALIDE);

    return this.scenarioService.getScenarioById(scenarioDTO.getId());
  }
}

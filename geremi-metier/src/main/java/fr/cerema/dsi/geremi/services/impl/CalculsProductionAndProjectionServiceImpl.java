package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.cerema.dsi.commons.services.CalculsCommunProductionAnneeAndProjectionService;
import fr.cerema.dsi.geremi.dto.ProductionZone;
import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.entities.RelScenarioZone;
import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.RelScenarioInstallationRepository;
import fr.cerema.dsi.geremi.repositories.RelScenarioMateriauRepository;
import fr.cerema.dsi.geremi.repositories.RelScenarioZoneRepository;
import fr.cerema.dsi.geremi.repositories.ResultatCalculRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.repositories.TerritoireRepository;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.CalculsProductionAndProjectionService;
import fr.cerema.dsi.geremi.services.CalculsProjectionBesoinService;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ResultatCalculDTO;
import fr.cerema.dsi.geremi.services.dto.ResultatZoneDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.ZoneProductionDetailsDTO;
import fr.cerema.dsi.geremi.services.mapper.ProductionZoneMapper;
import fr.cerema.dsi.geremi.services.mapper.ScenarioZoneMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CalculsProductionAndProjectionServiceImpl extends CalculsCommunProductionAnneeAndProjectionService
		implements CalculsProductionAndProjectionService {

  private final ScenarioRepository scenarioRepository;
  private final ZoneRepository zoneRepository;
  private final CalculsProjectionBesoinService calculsProjectionBesoinService;
  private final TerritoireRepository territoireRepository;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final ResultatCalculRepository resultatCalculRepository;
  private final RelScenarioDepartementService relScenarioDepartementService;
  private final ScenarioZoneMapper scenarioZoneMapper;
  private final RelScenarioZoneRepository relScenarioZoneRepository;
  private final ProductionZoneMapper productionZoneMapper;


  public CalculsProductionAndProjectionServiceImpl(ScenarioRepository scenarioRepository, RelScenarioInstallationRepository relScenarioInstallationRepository, RelResultatZoneService relResultatZoneService, RelScenarioMateriauRepository relScenarioMateriauRepository, TracabiliteEtapeService tracabiliteEtapeService, CalculsProjectionBesoinService calculsProjectionBesoinService, ResultatCalculRepository resultatCalculRepository, TerritoireRepository territoireRepository, RelScenarioDepartementService relScenarioDepartementService, ScenarioZoneMapper scenarioZoneMapper, RelScenarioZoneRepository relScenarioZoneRepository, ProductionZoneMapper productionZoneMapper, ZoneRepository zoneRepository) {

    super(relScenarioMateriauRepository, relScenarioInstallationRepository, relResultatZoneService, resultatCalculRepository);
    this.scenarioRepository = scenarioRepository;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.calculsProjectionBesoinService = calculsProjectionBesoinService;
    this.resultatCalculRepository = resultatCalculRepository;
    this.territoireRepository = territoireRepository;
    this.relScenarioDepartementService = relScenarioDepartementService;
    this.scenarioZoneMapper = scenarioZoneMapper;
    this.relScenarioZoneRepository = relScenarioZoneRepository;
    this.productionZoneMapper = productionZoneMapper;
    this.zoneRepository = zoneRepository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ScenarioDTO executeCalculProductionAnneeRef(Long idEtude, Long idScenario, int anneeRef, ScenarioDTO scenarioDTO) {

    scenarioRepository.findById(scenarioDTO.getId()).ifPresent(
        s -> {
          s.setPonderationSurfaceBeton(scenarioDTO.getPonderationSurfaceBeton());
          s.setPonderationSurfaceViabilite(scenarioDTO.getPonderationSurfaceViabilite());
          s.setDateMaj(LocalDateTime.now());
          scenarioRepository.save(s);
        }
    );

    this.relScenarioDepartementService.saveAll(idScenario, scenarioDTO.getRelScenarioDepartement());

    ResultatCalcul resultatCalcul = resultatCalculRepository.findByIdScenarioAndAnneeAndIfProjection(idScenario, anneeRef, false);

    //productions pour l'annee ref
    List<ProductionZone> productionsPrimaires = this.resultatCalculRepository.productionsPrimairesAnneeZonePeriodes(idScenario, anneeRef, anneeRef, anneeRef, anneeRef);
    Map<Long, List<ProductionZone>> productionsPrimairesAnneRef = productionZoneMapper.toMapAnneeMapZone(productionsPrimaires).get(anneeRef);

    List<ProductionZone> productionZoneStock = this.productionZoneStock(idScenario, anneeRef);
    List<ProductionZone> productionZoneSecondaireAutre = this.productionZoneSecondaireAutre(idScenario);
    List<ProductionZone> productionZonePrimaireAutre = this.productionZonePrimaireAutre(idScenario);

    List<RelResultatZone> resultatZoneExistant = new ArrayList<>();
    if (resultatCalcul == null) {
      Long idTerritoire = this.territoireRepository.findByEtude_Id(scenarioDTO.getEtudeDTO().getId()).getId();
      resultatCalcul = initNewResultatCalcul(scenarioDTO.getId(), idTerritoire, anneeRef);
    } else {
      resultatZoneExistant = this.relResultatZoneService.findByIdResultat(resultatCalcul.getId());
    }
    resultatCalcul.setIfProjection(false);

    List<Long> idZonesEtude = this.zoneRepository.listZoneIdByEtudeId(idEtude);

    scenarioDTO.setResultatsCalculs(new HashMap<>());
    ResultatCalculDTO resultatCalc = new ResultatCalculDTO(idEtude, idScenario, anneeRef);
    scenarioDTO.getResultatsCalculs().put(anneeRef, resultatCalc);
    resultatCalc.setResultatZones(new HashMap<>());

    for (Long idZone : idZonesEtude) {
      Long idResultat = resultatCalcul.getId();
      RelResultatZone relZone = resultatZoneExistant.stream().filter(z -> z.getIdZone().equals(idZone)).findFirst().orElseGet(() -> this.initNewRelResultatZone(idResultat, idZone));

      BigDecimal productionZonePrimaireZone = Optional.ofNullable(productionsPrimairesAnneRef.get(idZone)).map(l -> l.stream().map(ProductionZone::getProductionZone).reduce(BigDecimal.ZERO, BigDecimal::add)).orElse(BigDecimal.ZERO);
      BigDecimal productionZonePrimaireAutreZone = productionZonePrimaireAutre.stream().filter(p -> p.getIdZone().equals(idZone)).map(ProductionZone::getProductionZone).findFirst().orElse(BigDecimal.ZERO);
      BigDecimal productionZoneSecondaireAutreZone = productionZoneSecondaireAutre.stream().filter(p -> p.getIdZone().equals(idZone)).map(ProductionZone::getProductionZone).findFirst().orElse(BigDecimal.ZERO);
      BigDecimal productionZoneStockZone = productionZoneStock.stream().filter(p -> p.getIdZone().equals(idZone)).map(ProductionZone::getProductionZone).findFirst().orElse(BigDecimal.ZERO);

      BigDecimal productionZonePrimaireTotal = productionZonePrimaireZone.add(productionZonePrimaireAutreZone).setScale(3, RoundingMode.HALF_UP);
      BigDecimal productionZoneSecondaireTotal = productionZoneStockZone.add(productionZoneSecondaireAutreZone).setScale(3, RoundingMode.HALF_UP);
      BigDecimal productionZoneTotal = productionZonePrimaireTotal.add(productionZoneSecondaireTotal).setScale(3, RoundingMode.HALF_UP);

      // mise a jour des entites pour persistance en base
      relZone.setProductionZonePrimaire(productionZonePrimaireTotal.doubleValue());
      relZone.setProductionZoneSecondaire(productionZoneSecondaireTotal.doubleValue());
      relZone.setProductionZoneTotal(productionZoneTotal.doubleValue());
      if (BigDecimal.ZERO.compareTo(productionZoneTotal) != 0) {
        relZone.setPourcentProductionZoneSecondaire(productionZoneSecondaireTotal.multiply(BigDecimal.valueOf(100)).divide(productionZoneTotal, 2, RoundingMode.HALF_UP).doubleValue());
      } else {
        relZone.setPourcentProductionZoneSecondaire(BigDecimal.valueOf(100L).doubleValue());
      }

      //Ajout du resultat Calcul de la zone dans le scenario pour le retour
      ResultatZoneDTO resZone = new ResultatZoneDTO(idEtude, idScenario, anneeRef, idZone);

      resZone.setProductionZonePrimaireTotal(relZone.getProductionZonePrimaire());
      resZone.setProductionZoneSecondaireTotal(relZone.getProductionZoneSecondaire());
      resZone.setProductionZoneTotal(relZone.getProductionZoneTotal());
      resZone.setPourcentageProductionZoneSecondaire(relZone.getPourcentProductionZoneSecondaire());

      resultatCalc.getResultatZones().put(idZone, resZone);
    }

    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, idScenario, Etape.HYPOTHESE_VENTILATION_SCENARIO, EtatEtape.VALIDE);
    this.tracabiliteEtapeService.addTracabiliteEtape(idEtude, idScenario, Etape.HYPOTHESE_PROJECTION_SCENARIO, EtatEtape.NON_RENSEIGNE);


    return scenarioDTO;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ScenarioDTO projectionScenario(ScenarioDTO scenarioDTO) {

    // Récupération de l'année de référence et de l'année de fin
    Integer anneeRef = scenarioDTO.getEtudeDTO().getAnneeRef();
    Integer anneeFin = scenarioDTO.getEtudeDTO().getAnneeFin();

    //persitance des pourcentages echance saisi dans les hypothèses de projection
    for (ZoneProductionDetailsDTO zoneProductionDetailsDTO : scenarioDTO.getZoneProductionDetails()) {
      RelScenarioZone entity = this.relScenarioZoneRepository.findByIdScenarioAndIdZone(scenarioDTO.getId(), zoneProductionDetailsDTO.getId());
      if (entity == null) {
        entity = scenarioZoneMapper.toEntity(zoneProductionDetailsDTO);
        entity.setIdScenario(scenarioDTO.getId());
        this.relScenarioZoneRepository.save(entity);
      } else {
        entity.setDateMaj(LocalDateTime.now());
        entity.setProjectionScondaireEcheance(zoneProductionDetailsDTO.getPourcentage2());
      }
    }

    //récupération de l'idTerritoire
    Long idTerritoire = this.territoireRepository.findByEtude_Id(scenarioDTO.getEtudeDTO().getId()).getId();

    // Créer une Map à partir de la liste des ResultatCalcul existants avec l'annee comme clé et l'objet ResultatCalcul comme valeur
    Map<Integer, ResultatCalcul> resultatCalculMap = resultatCalculRepository.findByIdScenarioForCalcul(scenarioDTO.getId()).stream().collect(Collectors.toMap(ResultatCalcul::getAnnee, Function.identity()));

    // Map des besoins zone par annee. Toutes les années et toutes les zones sont initialisées
    Map<Integer, ResultatCalculDTO> resultatCalculDTOMap = this.calculsProjectionBesoinService.calculerProjectionBesoin(scenarioDTO, idTerritoire);

    //pour l'anneeRef, on reprend les resultatZone Existants en base
    //map des resultatZone par zone pour l'anneeRef
    Map<Long, RelResultatZone> resultatZonesMap = this.relResultatZoneService.findByIdResultat(resultatCalculMap.get(anneeRef).getId()).stream().collect(Collectors.toMap(RelResultatZone::getIdZone, Function.identity()));

    for (ResultatZoneDTO resZoneDTO : resultatCalculDTOMap.get(anneeRef).getResultatZones().values()) {
      resZoneDTO.setProductionZonePrimaireTotal(resultatZonesMap.get(resZoneDTO.getIdZone()).getProductionZonePrimaire());
      resZoneDTO.setProductionZoneSecondaireTotal(resultatZonesMap.get(resZoneDTO.getIdZone()).getProductionZoneSecondaire());
      resZoneDTO.setProductionZoneTotal(resultatZonesMap.get(resZoneDTO.getIdZone()).getProductionZoneTotal());
      resZoneDTO.setPourcentageProductionZoneSecondaire(resultatZonesMap.get(resZoneDTO.getIdZone()).getPourcentProductionZoneSecondaire());
    }

    List<ProductionZone> productionZonePrimaireAutre = this.productionZonePrimaireAutre(scenarioDTO.getId());

    //1) projection de productions pour toutes les annees de l'étude supérieures à anneeRef en prenant les destinations de l'anneeRef comme base de calcul
    List<ProductionZone> productionsPrimaires = this.resultatCalculRepository.productionsPrimairesAnneeZonePeriodes(scenarioDTO.getId(), anneeRef + 1, anneeFin, anneeRef, anneeRef);
    Map<Integer, Map<Long, List<ProductionZone>>> productionsPrimairesAnneeZone = productionZoneMapper.toMapAnneeMapZone(productionsPrimaires);

    Map<Long, BigDecimal> prodZonePrimaireTotalEcheance = new HashMap<>();
    //integration des prod primaire des annees suivantes dans la map resultatCalculDTOMap
    for (Integer annee = anneeRef + 1; annee <= anneeFin; annee++) {
      for (ResultatZoneDTO resultatZoneDTO : resultatCalculDTOMap.get(annee).getResultatZones().values()) {

        BigDecimal productionZonePrimaireZone = Optional.ofNullable(productionsPrimairesAnneeZone.get(annee)).map(h -> h.get(resultatZoneDTO.getIdZone())).map(l -> l.stream().map(ProductionZone::getProductionZone).reduce(BigDecimal.ZERO, BigDecimal::add)).orElse(BigDecimal.ZERO);
        BigDecimal productionZonePrimaireAutreZone = productionZonePrimaireAutre.stream().filter(p -> p.getIdZone().equals(resultatZoneDTO.getIdZone())).map(ProductionZone::getProductionZone).findFirst().orElse(BigDecimal.ZERO);
        BigDecimal productionZonePrimaireTotal = productionZonePrimaireZone.add(productionZonePrimaireAutreZone).setScale(3, RoundingMode.HALF_UP);

        resultatZoneDTO.setProductionZonePrimaireTotal(productionZonePrimaireTotal.doubleValue());

        //on stock pour chaque zone le productionZonePrimaireTotal à l'annee d'echeance de l'etude
        if (annee.equals(anneeFin)) {
          prodZonePrimaireTotalEcheance.put(resultatZoneDTO.getIdZone(), productionZonePrimaireTotal);
        }
      }
    }

    //2) Calcul de la production SECONDAIRE de la zone (ProductionZoneSecondaireTotal) :
    for (Integer annee = anneeRef + 1; annee <= anneeFin; annee++) {
      for (ResultatZoneDTO resultatZoneDTO : resultatCalculDTOMap.get(annee).getResultatZones().values()) {
        BigDecimal ProductionZonePrimaireEcheance = prodZonePrimaireTotalEcheance.get(resultatZoneDTO.getIdZone());

        Long tauxHypProjZone = scenarioDTO.getZoneProductionDetails().stream().filter(zpd -> zpd.getId().equals(resultatZoneDTO.getIdZone())).findFirst().get().getPourcentage2();

        //le taux ne peut pas être de 100% (bloqué en saisie coté front)
        BigDecimal productionZoneSecondaireEcheance = ProductionZonePrimaireEcheance.multiply(BigDecimal.valueOf(tauxHypProjZone).divide(BigDecimal.valueOf(100L - tauxHypProjZone), MathContext.DECIMAL32));

        BigDecimal productionZoneSecondaireTotalAnneeRefence = BigDecimal.valueOf(resultatCalculDTOMap.get(anneeRef).getResultatZones().get(resultatZoneDTO.getIdZone()).getProductionZoneSecondaireTotal());

        BigDecimal productionZoneSecondaireTotal;
        if (annee.compareTo(anneeFin) == 0) {
          productionZoneSecondaireTotal = productionZoneSecondaireEcheance.setScale(3, RoundingMode.HALF_UP);
        } else {
          productionZoneSecondaireTotal = productionZoneSecondaireTotalAnneeRefence.add(BigDecimal.valueOf(annee - anneeRef).multiply(productionZoneSecondaireEcheance.subtract(productionZoneSecondaireTotalAnneeRefence).divide(BigDecimal.valueOf(anneeFin - anneeRef), MathContext.DECIMAL32))).setScale(3, RoundingMode.HALF_UP);
        }

        resultatZoneDTO.setProductionZoneSecondaireTotal(productionZoneSecondaireTotal.doubleValue());

        //3) Calcul de la production totale de la zone et du pourcentage de production de secondaire de la zone
        BigDecimal productionZoneTotal = productionZoneSecondaireTotal.add(BigDecimal.valueOf(resultatZoneDTO.getProductionZonePrimaireTotal())).setScale(3, RoundingMode.HALF_UP);
        resultatZoneDTO.setProductionZoneTotal(productionZoneTotal.doubleValue());

        if (productionZoneTotal.compareTo(BigDecimal.ZERO) != 0) {
          BigDecimal pourcentageProductionZoneSecondaire = productionZoneSecondaireTotal.multiply(BigDecimal.valueOf(100)).divide(productionZoneTotal, 2, RoundingMode.HALF_UP);
          resultatZoneDTO.setPourcentageProductionZoneSecondaire(pourcentageProductionZoneSecondaire.doubleValue());
        } else {
          resultatZoneDTO.setPourcentageProductionZoneSecondaire(BigDecimal.valueOf(100L).doubleValue());
        }

      }
    }

    //Calcul des prods Intra et brut
    List<ProductionZone> productionsInterne = this.resultatCalculRepository.getProductionsInterneZone(scenarioDTO.getId());
    Map<Integer, Map<Long, List<ProductionZone>>> productionsInterneAnneeZone = productionZoneMapper.toMapAnneeMapZone(productionsInterne);

    List<ProductionZone> productionsPrimaireBrute = this.resultatCalculRepository.getProductionsZonePrimaireBrute(scenarioDTO.getId(), anneeRef, anneeFin, anneeRef, anneeRef);
    Map<Integer, Map<Long, List<ProductionZone>>> productionsPrimaireBruteAnneeZone = productionZoneMapper.toMapAnneeMapZone(productionsPrimaireBrute);

    // on integre l'intra et le brut à la resultatCalculDTOMap
    for (Integer annee = anneeRef; annee <= anneeFin; annee++) {
      for (ResultatZoneDTO resultatZoneDTO : resultatCalculDTOMap.get(annee).getResultatZones().values()) {
        BigDecimal productionZonePrimaireIntra = Optional.ofNullable(productionsInterneAnneeZone.get(annee)).map(h -> h.get(resultatZoneDTO.getIdZone())).map(l -> l.stream().map(ProductionZone::getProductionZone).reduce(BigDecimal.ZERO, BigDecimal::add)).orElse(BigDecimal.ZERO);
        resultatZoneDTO.setProductionZonePrimaireIntra(productionZonePrimaireIntra.setScale(3, RoundingMode.HALF_UP).doubleValue());

        BigDecimal productionZonePrimaireBrute = Optional.ofNullable(productionsPrimaireBruteAnneeZone.get(annee)).map(h -> h.get(resultatZoneDTO.getIdZone())).flatMap(l -> l.stream().map(ProductionZone::getProductionZone).findFirst()).orElse(BigDecimal.ZERO);
        resultatZoneDTO.setProductionZonePrimaireBrute(productionZonePrimaireBrute.setScale(3, RoundingMode.HALF_UP).doubleValue());
      }
    }

    //Par année : persitance du calcul des zones + calcul des somme des zones pour le territoire
    for (ResultatCalculDTO resultatCalculDTO : resultatCalculDTOMap.values()) {
      BigDecimal productionTerritoirePrimaireTotal = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getProductionZonePrimaireTotal())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal productionTerritoirePrimaireIntra = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getProductionZonePrimaireIntra())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal productionTerritoirePrimaireBrute = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getProductionZonePrimaireBrute())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal productionTerritoireSecondaireTotal = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getProductionZoneSecondaireTotal())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal productionTerritoireTotal = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getProductionZoneTotal())).reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal pourcentProductionTerritoireSecondaire;
      if (productionTerritoireTotal.compareTo(BigDecimal.ZERO) != 0) {
        pourcentProductionTerritoireSecondaire = productionTerritoireSecondaireTotal.multiply(BigDecimal.valueOf(100)).divide(productionTerritoireTotal, 2, RoundingMode.HALF_UP);
      } else {
        pourcentProductionTerritoireSecondaire = BigDecimal.valueOf(100L);
      }

      BigDecimal besoinTerritoireTotalChantier = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getBesoinZoneTotalChantier())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal besoinTerritoirePrimaire = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getBesoinZonePrimaire())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal besoinTerritoireSecondaire = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getBesoinZoneSecondaire())).reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal besoinTerritoireTotal = resultatCalculDTO.getResultatZones().values().stream().map(rz -> BigDecimal.valueOf(rz.getBesoinZoneTotal())).reduce(BigDecimal.ZERO, BigDecimal::add);

      //Recupération du resultatCalcul existant pour l'annee, initialisation si absent
      ResultatCalcul resultatCalcul = Optional.ofNullable(resultatCalculMap.get(resultatCalculDTO.getAnnee())).orElseGet(() -> initNewResultatCalcul(scenarioDTO.getId(), idTerritoire, resultatCalculDTO.getAnnee()));
      if (!resultatCalculDTO.getAnnee().equals(anneeRef)) {
        resultatCalcul.setIfProjection(true);
      }
      resultatCalcul.setProductionTerritoirePrimaire(productionTerritoirePrimaireTotal.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoirePrimaireIntra(productionTerritoirePrimaireIntra.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoirePrimaireBrut(productionTerritoirePrimaireBrute.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoireSecondaire(productionTerritoireSecondaireTotal.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setProductionTerritoireTotal(productionTerritoireTotal.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setPourcentProductionTerritoireSecondaire(pourcentProductionTerritoireSecondaire.setScale(2, RoundingMode.HALF_UP).doubleValue());

      resultatCalcul.setBesoinTerritoireTotalChantier(besoinTerritoireTotalChantier.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setBesoinTerritoirePrimaire(besoinTerritoirePrimaire.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setBesoinTerritoireSecondaire(besoinTerritoireSecondaire.setScale(3, RoundingMode.HALF_UP).doubleValue());
      resultatCalcul.setBesoinTerritoireTotal(besoinTerritoireTotal.setScale(3, RoundingMode.HALF_UP).doubleValue());


      resultatCalculDTO.setProductionTerritoirePrimaireTotal(resultatCalcul.getProductionTerritoirePrimaire());
      resultatCalculDTO.setProductionTerritoirePrimaireIntra(resultatCalcul.getProductionTerritoirePrimaireIntra());
      resultatCalculDTO.setProductionTerritoirePrimaireBrute(resultatCalcul.getProductionTerritoirePrimaireBrut());
      resultatCalculDTO.setProductionTerritoireSecondaireTotal(resultatCalcul.getProductionTerritoireSecondaire());
      resultatCalculDTO.setProductionTerritoireTotal(resultatCalcul.getProductionTerritoireTotal());
      resultatCalculDTO.setPourcentProductionTerritoireSecondaire(resultatCalcul.getPourcentProductionTerritoireSecondaire());

      resultatCalculDTO.setBesoinTerritoireTotalChantier(resultatCalcul.getBesoinTerritoireTotalChantier());
      resultatCalculDTO.setBesoinTerritoirePrimaire(resultatCalcul.getBesoinTerritoirePrimaire());
      resultatCalculDTO.setBesoinTerritoireSecondaire(resultatCalcul.getBesoinTerritoireSecondaire());
      resultatCalculDTO.setBesoinTerritoireTotal(resultatCalcul.getBesoinTerritoireTotal());

      resultatCalcul.setDateMaj(LocalDateTime.now());

      Map<Long, RelResultatZone> resultatZoneExistantAnnee = new HashMap<>();
      if (resultatCalculMap.get(resultatCalculDTO.getAnnee()) != null) {
        resultatZoneExistantAnnee = this.relResultatZoneService.findByIdResultat(resultatCalcul.getId()).stream().collect(Collectors.toMap(RelResultatZone::getIdZone, Function.identity()));
      }

      for (ResultatZoneDTO resultatZoneDTO : resultatCalculDTO.getResultatZones().values()) {
        RelResultatZone relResultatZone = Optional.ofNullable(resultatZoneExistantAnnee.get(resultatZoneDTO.getIdZone())).orElseGet(() -> initNewRelResultatZone(resultatCalcul.getId(), resultatZoneDTO.getIdZone()));
        relResultatZone.setBesoinZoneTotalChantier(resultatZoneDTO.getBesoinZoneTotalChantier());
        relResultatZone.setBesoinZonePrimaire(resultatZoneDTO.getBesoinZonePrimaire());
        relResultatZone.setBesoinZoneSecondaire(resultatZoneDTO.getBesoinZoneSecondaire());
        relResultatZone.setBesoinZoneTotal(resultatZoneDTO.getBesoinZoneTotal());
        relResultatZone.setProductionZonePrimaire(resultatZoneDTO.getProductionZonePrimaireTotal());
        relResultatZone.setProductionZoneSecondaire(resultatZoneDTO.getProductionZoneSecondaireTotal());
        relResultatZone.setProductionZoneTotal(resultatZoneDTO.getProductionZoneTotal());
        relResultatZone.setProductionZonePrimaireIntra(resultatZoneDTO.getProductionZonePrimaireIntra());
        relResultatZone.setProductionZonePrimaireBrut(resultatZoneDTO.getProductionZonePrimaireBrute());
        relResultatZone.setPourcentProductionZoneSecondaire(resultatZoneDTO.getPourcentageProductionZoneSecondaire());

        relResultatZone.setDateMaj(LocalDateTime.now());

      }

    }

    scenarioDTO.setResultatsCalculs(resultatCalculDTOMap);

    // Tracabilite
    this.tracabiliteEtapeService.addTracabiliteEtape(scenarioDTO.getEtudeDTO().getId(), scenarioDTO.getId(), Etape.HYPOTHESE_PROJECTION_SCENARIO, EtatEtape.VALIDE);

    return scenarioDTO;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ScenarioDTO recalculSuiviScenarioProdReelle(ScenarioDTO scenarioDTO) {

    // Récupération de l'année de référence et de l'année de fin
    Integer anneeRef = scenarioDTO.getEtudeDTO().getAnneeRef();
    Integer anneeFin = scenarioDTO.getEtudeDTO().getAnneeFin();

    //récupération de l'idTerritoire
    Long idTerritoire = this.territoireRepository.findByEtude_Id(scenarioDTO.getEtudeDTO().getId()).getId();

    //determination des annees don les destinations sont disponnibles, et pour lesquelles la production réelle n'est pas calculée.
    Tuple annesNonCalculees = resultatCalculRepository.getAnneesSuiviNonCalculees(scenarioDTO.getId());

    Map<Integer, Map<Long, List<ProductionZone>>> productionsPrimairesAnneeZone = new HashMap<>();
    List<ProductionZone> productionZonePrimaireAutre = new ArrayList<>();
    Map<Integer, Map<Long, List<ProductionZone>>> productionsPrimaireBruteAnneeZone  = new HashMap<>();
    Integer anneeDebutRecalcul = 0;
    Integer anneeFinRecalcul = 0;
    if (annesNonCalculees != null && annesNonCalculees.get("min", Integer.class) != null && annesNonCalculees.get("max", Integer.class) != null) {
      anneeDebutRecalcul = annesNonCalculees.get("min", Integer.class);
      anneeFinRecalcul = annesNonCalculees.get("max", Integer.class);

      //productions primaires reelle pour toutes les annees de l'étude dont les destinations sont disponibles et pour lesquelles la production réelle n'est pas calculée.
      List<ProductionZone> productionsPrimaires = this.resultatCalculRepository.productionsPrimairesAnneeZonePeriodes(scenarioDTO.getId(), anneeDebutRecalcul, anneeFinRecalcul, anneeDebutRecalcul, anneeFinRecalcul);
      productionsPrimairesAnneeZone = productionZoneMapper.toMapAnneeMapZone(productionsPrimaires);

      productionZonePrimaireAutre = this.productionZonePrimaireAutre(scenarioDTO.getId());

      List<ProductionZone> productionsPrimaireBrute = this.resultatCalculRepository.getProductionsZonePrimaireBrute(scenarioDTO.getId(), anneeDebutRecalcul, anneeFinRecalcul, anneeDebutRecalcul, anneeFinRecalcul);
      productionsPrimaireBruteAnneeZone = productionZoneMapper.toMapAnneeMapZone(productionsPrimaireBrute);

    }

    // Créer une Map à partir de la liste des ResultatCalcul reels existants avec l'annee comme clé et l'objet ResultatCalcul comme valeur
    Map<Integer, ResultatCalcul> resultatCalculMap = resultatCalculRepository.findByIdScenarioReelPeriode(scenarioDTO.getId(), anneeRef + 1, anneeFin).stream().collect(Collectors.toMap(ResultatCalcul::getAnnee, Function.identity()));

    // Pour chaque annee de l'etude
    for (Integer annee = anneeRef + 1 ; annee <= anneeFin; annee++) {

      // si on a un resultat de calcul préexistant on le map
      if (resultatCalculMap.containsKey(annee))  {
        ResultatCalcul resultatCalcul = resultatCalculMap.get(annee);

        //map des resultatZone existant par zone
        Map<Long, RelResultatZone> resultatZonesMap = this.relResultatZoneService.findByIdResultat(resultatCalcul.getId()).stream().collect(Collectors.toMap(RelResultatZone::getIdZone, Function.identity()));

        for (RelResultatZone resultatZone : resultatZonesMap.values() ) {
          scenarioDTO.getResultatsCalculs().get(annee).getResultatZones().get(resultatZone.getIdZone()).setProductionZonePrimaireTotalReelle(resultatZone.getProductionZonePrimaire());
          scenarioDTO.getResultatsCalculs().get(annee).getResultatZones().get(resultatZone.getIdZone()).setProductionZonePrimaireBruteReelle(resultatZone.getProductionZonePrimaireBrut());
        }
      }
      // si l'annee a été recalculée, on map et on persite le resultat
      else if (annee >= anneeDebutRecalcul && annee <= anneeFinRecalcul) {
        ResultatCalcul resultatCalcul = initNewResultatCalcul(scenarioDTO.getId(), idTerritoire, annee);
        resultatCalcul.setIfProjection(false);

        for (ResultatZoneDTO resultatZoneDTO : scenarioDTO.getResultatsCalculs().get(annee).getResultatZones().values()) {
          RelResultatZone relResultatZone = initNewRelResultatZone(resultatCalcul.getId(), resultatZoneDTO.getIdZone());

          BigDecimal productionZonePrimaireAutreZone = productionZonePrimaireAutre.stream().filter(p -> p.getIdZone().equals(resultatZoneDTO.getIdZone())).map(ProductionZone::getProductionZone).findFirst().orElse(BigDecimal.ZERO).setScale(3, RoundingMode.HALF_UP);

          BigDecimal productionZonePrimaireTotalRelle = Optional.ofNullable(productionsPrimairesAnneeZone.get(annee)).map(h -> h.get(resultatZoneDTO.getIdZone())).map(l -> l.stream().map(ProductionZone::getProductionZone).reduce(BigDecimal.ZERO, BigDecimal::add)).orElse(BigDecimal.ZERO);
          resultatZoneDTO.setProductionZonePrimaireTotalReelle(productionZonePrimaireTotalRelle.add(productionZonePrimaireAutreZone).setScale(3, RoundingMode.HALF_UP).doubleValue());
          relResultatZone.setProductionZonePrimaire(resultatZoneDTO.getProductionZonePrimaireTotalReelle());

          BigDecimal productionZonePrimaireBruteRelle = Optional.ofNullable(productionsPrimaireBruteAnneeZone.get(annee)).map(h -> h.get(resultatZoneDTO.getIdZone())).flatMap(l -> l.stream().map(ProductionZone::getProductionZone).findFirst()).orElse(BigDecimal.ZERO);
          resultatZoneDTO.setProductionZonePrimaireBruteReelle(productionZonePrimaireBruteRelle.setScale(3, RoundingMode.HALF_UP).doubleValue());
          relResultatZone.setProductionZonePrimaireBrut(resultatZoneDTO.getProductionZonePrimaireBruteReelle());
        }
      }
    }
    return scenarioDTO;
  }

}

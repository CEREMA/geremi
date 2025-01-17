package fr.cerema.dsi.geremi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.cerema.dsi.geremi.services.CarriereAffichageConfigService;
import fr.cerema.dsi.geremi.services.CarriereDetailConfigService;
import fr.cerema.dsi.geremi.services.EtablissementService;
import fr.cerema.dsi.geremi.services.dto.CarriereDetailDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wololo.geojson.FeatureCollection;


@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class EtablissementController {

  private CarriereDetailConfigService carriereDetailConfigService;

  private CarriereAffichageConfigService carriereAffichageConfigService;

  private EtablissementService etablissementService;

  public EtablissementController(CarriereDetailConfigService carriereDetailConfigService, CarriereAffichageConfigService carriereAffichageConfigService, EtablissementService etablissementService) {
    this.carriereDetailConfigService = carriereDetailConfigService;
    this.carriereAffichageConfigService = carriereAffichageConfigService;
    this.etablissementService = etablissementService;
  }



  /**
   * On requête la totalité des carrières afin de mettre en cache
   *
   * @return
   */
  @GetMapping(value ="/etablissement", produces = "application/json;charset=UTF-8")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<FeatureCollection> getEtablissementAnnee(@RequestParam("annee") String annee, @RequestParam("type") String type) {
    FeatureCollection featureCollection;
    if ("EtablissementNaturel".equals(type)){
      featureCollection = this.etablissementService.findEtablissementsAnneeEtOrigineMat(annee, "Matériaux naturels");
    } else if ("EtablissementRecycle".equals(type)){
      featureCollection = this.etablissementService.findEtablissementsAnneeEtOrigineMat(annee, "Matériaux recyclés");
    } else if ("EtablissementNaturelRecycle".equals(type)){
      featureCollection = this.etablissementService.findEtablissementsAnneeEtOrigineMat(annee, "Matériaux naturels et recyclés");
    } else {
      featureCollection = this.etablissementService.findEtablissementsAnnee(annee);
    }

    EtablissementController.log.debug("Retour de {} etablissement pour l'année {} et le type {}", featureCollection.getFeatures().length, annee, type);
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .cacheControl(CacheControl.noCache())
      .body(featureCollection);
  }

  @GetMapping(value ="/etablissement/{annee}/{idEtude}", produces = "application/json;charset=UTF-8")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<FeatureCollection> getEtablissementEtude(@PathVariable("annee") String annee, @PathVariable("idEtude") Long idEtude, @RequestParam("territoireSeul") Boolean territoireSeul) {
    FeatureCollection featureCollection = this.etablissementService.findEtablissementsAnneeEtude(annee, idEtude, territoireSeul);

    EtablissementController.log.debug("Retour de {} etablissement pour l'année {} et l'étude {}", featureCollection.getFeatures().length, annee, idEtude);
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .cacheControl(CacheControl.noCache())
      .body(featureCollection);
  }

    @GetMapping(value = "/etablissement/config/detail", produces = "application/json;charset=UTF-8")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
    public ResponseEntity<Map<String,CarriereDetailDTO>> getCarriereDetailConfig() {
        EtablissementController.log.debug("Récupération des carrières libellés détails");
        Map<String, CarriereDetailDTO> carriereDetailConfigList = new HashMap<>();

        this.carriereDetailConfigService.getCarriereDetailConfig()
            .forEach( c -> carriereDetailConfigList.put(c.getAttr(), c));

    EtablissementController.log.debug("Retour des détails carrières");
    return new ResponseEntity<>(carriereDetailConfigList, HttpStatus.OK);
    }

    @GetMapping( value = "/etablissement/config/affichage", produces = "application/json;charset=UTF-8")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
    public ResponseEntity<Map<String,List<String>>> getCarriereAffichageConfig() {
        EtablissementController.log.debug("Récupération des affichages carrières");
        Map<String,List<String>> carriereAffichageConfigList = new HashMap<>();

        carriereAffichageConfigList.put("principal",this.carriereAffichageConfigService.getCarriereAffichagePrincipalConfig());
        carriereAffichageConfigList.put("activite",this.carriereAffichageConfigService.getCarriereAffichageActiviteConfig());
        carriereAffichageConfigList.put("destination",this.carriereAffichageConfigService.getCarriereAffichageDestinationConfig());
        carriereAffichageConfigList.put("traitementDechet",this.carriereAffichageConfigService.getCarriereAffichageTraitementDechetConfig());

    EtablissementController.log.debug("Retour des détails carrières");
    return new ResponseEntity<>(carriereAffichageConfigList, HttpStatus.OK);
  }

  @GetMapping("/etablissement/config/tooltip")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<List<String>> getCarriereAffichageTooltipConfig() {
        EtablissementController.log.debug("Récupération des affichages tooltips carrières");
        List<String> carriereAffichageTooltipConfigList = this.carriereAffichageConfigService.getCarriereAffichageTooltipConfig();

        EtablissementController.log.debug("Retour des détails tooltips carrières");
        return new ResponseEntity<>(carriereAffichageTooltipConfigList, HttpStatus.OK);
  }

    @GetMapping("/etablissement/annees")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
    public ResponseEntity<List<String>> getDistinctAnnees() {
        List<String> annees = this.etablissementService.findDistinctAnnees();
        return new ResponseEntity<>(annees, HttpStatus.OK);
    }
}

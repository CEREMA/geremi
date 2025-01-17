package fr.cerema.dsi.geremi.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.cerema.dsi.geremi.services.DepartementService;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
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
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DepartementController {

  private final Logger logger = LoggerFactory.getLogger(DepartementController.class);

  @Autowired
  private DepartementService departementService;

  @GetMapping("/departement")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<FeatureCollection> getDepartement(@RequestParam List<String> bbox, @RequestParam String precision) {

    logger.debug("Récupération des départements dans la zone suivante : Xmin = {}, Ymin = {}, Xmax = {}, Ymax = {}, Précision = {}", bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3), precision);
    List<Feature> featureArrayList = this.departementService.findInBox(Double.parseDouble(bbox.get(0)), Double.parseDouble(bbox.get(1)), Double.parseDouble(bbox.get(2)), Double.parseDouble(bbox.get(3)), new BigDecimal(precision));
    Feature[] features = new Feature[featureArrayList.size()];
    FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
    logger.debug("Retour de {} départements dans la zone spécifiée", featureArrayList.size());
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .body(featureCollection);
  }

  @GetMapping("/departement/partiel/{idEtude}")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<RelScenarioDepartementDTO>> getDepartementsPartielsEtude(@PathVariable Long idEtude) {
    List<RelScenarioDepartementDTO> relScenarioDepartementDTOs = this.departementService.findDepartementPartielInEtude(idEtude);
    logger.debug("Retour de {} départements dans l'étude spécifiée", relScenarioDepartementDTOs.size());
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .body(relScenarioDepartementDTOs);
  }
}

package fr.cerema.dsi.geremi.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.services.BassinVieService;
import fr.cerema.dsi.geremi.services.CommuneService;
import fr.cerema.dsi.geremi.services.DepartementService;
import fr.cerema.dsi.geremi.services.EpciService;
import fr.cerema.dsi.geremi.services.ZoneEmploiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class SelectionZonageController {

    // Service
    @Autowired
    private DepartementService departementService;
    @Autowired
    private ZoneEmploiService zoneEmploiService;
    @Autowired
    private BassinVieService bassinVieService;
    @Autowired
    private EpciService epciService;
    @Autowired
    private CommuneService communeService;


    @Operation(summary = "Récupère la liste des zones à l'intérieur selon la liste d'id de territoire", description = "Retourne une liste des features du type zonage comprise dans la liste_id de territoires choisis")
    @GetMapping("/selectionzonage")
    @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
    public ResponseEntity<FeatureCollection> getSelectionZonage(@RequestParam String zonage, @RequestParam String territoire, @RequestParam List<Long> liste_id, @RequestParam String precision) {
	    SelectionZonageController.log.debug("Récupération de la liste des zones comprise dans les territoires : zonage = {}, territoire = {}, liste id des terrioitres = {}, Précision = {}", zonage, territoire, liste_id, precision);
      List<Feature> featureArrayList = new ArrayList<>();
      //definition d'une taillle de grille par defaut
      BigDecimal defaultPrecision =  new BigDecimal("0.000001");
      switch (zonage) {
        case "departement":
          featureArrayList = this.departementService.findInListeRegion(liste_id, new BigDecimal(precision));
          break;
        case "zoneemploi":
          featureArrayList = this.zoneEmploiService.findSelectionTerritoireInterieurExterieur(territoire, liste_id,new BigDecimal(precision));
          break;
        case "bassinvie":
          featureArrayList = this.bassinVieService.findSelectionTerritoireInterieurExterieur(territoire, liste_id, new BigDecimal(precision));
          break;
        case "epci":
          featureArrayList = this.epciService.findSelectionTerritoireInterieurExterieur(territoire, liste_id, new BigDecimal(precision));
          break;
        case "commune":
          featureArrayList = this.communeService.findInListeTerritoire(territoire, liste_id, new BigDecimal(precision));
          break;
      }
      Feature[] features = new Feature[featureArrayList.size()];
      FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));

      return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

}

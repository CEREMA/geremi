package fr.cerema.dsi.geremi.controllers;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.cerema.dsi.geremi.services.dto.ZoneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.ZoneService;
import fr.cerema.dsi.geremi.services.dto.TerritoireDto;
import fr.cerema.dsi.geremi.services.mapper.TerritoireMapper;
import fr.cerema.dsi.geremi.services.mapper.ZoneMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class TerritoireController {


  @Autowired
  private TerritoireService territoireService;
  @Autowired
  private TerritoireMapper territoireMapper;
  @Autowired
  private ZoneService zoneService;
  @Autowired
  private ZoneMapper zoneMapper;

  @Operation(summary = "Ajout d'un Territoire", description = "Ajout d'un territoire plus son zonage associé")
  @PostMapping("/territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<TerritoireDto> addTerritoire(@RequestBody TerritoireDto territoireDTO) {
    log.debug("Requête REST pourGeometry ajouter un territoire");
    log.debug("Territoire DTO : {}", territoireDTO.toString());

    if (territoireDTO.getIdEtude() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    territoireDTO.setIdTerritoire(this.territoireService.createTerritoire(territoireDTO).getId());

    return new ResponseEntity<>(territoireDTO, HttpStatus.CREATED);
  }

  @Operation(summary = "Validation d'un import Territoire", description = "Validation d'un import Territoire")
  @PostMapping("/territoire/validation")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<TerritoireDto> validationTerritoire(@RequestBody TerritoireDto territoireDTO) {
    log.debug("Requête REST pour valider un territoire");
    log.debug("Territoire DTO : {}", territoireDTO.toString());

    if (territoireDTO.getIdEtude() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    territoireDTO.setIdTerritoire(this.territoireService.validationCreationTerritoire(territoireDTO).getId());
    return new ResponseEntity<>(territoireDTO, HttpStatus.OK);
  }

  @GetMapping("/zone/feature")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<FeatureCollection> getZonesFeatures(@RequestParam("idEtude") Long id,@RequestParam("precision") String precision) {
    log.debug("Requête REST pour récupérer les zones d'une étude");
    List<Feature> featureArrayList = this.territoireService.getZoneForEtude(id,precision).stream()
      .map(this.zoneMapper::toFeature)
      .toList();
    Feature[] features = new Feature[featureArrayList.size()];
    FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
    log.debug(featureArrayList.toString());
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .cacheControl(CacheControl.noCache())
      .body(featureCollection);
  }

  @GetMapping("/zone")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
    public ResponseEntity<List<ZoneDTO>> getZones(@RequestParam("idEtude") Long id) {
    log.debug("Requête REST pour récupérer les zones d'une étude");
    List<ZoneDTO> zoneDTOs = this.territoireService.findZoneByEtude(id);
    return ResponseEntity.ok()
      .body(zoneDTOs);
  }

  @GetMapping("/zone/before")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getZonesBefore(@RequestParam("idEtude") Long id,@RequestParam("precision") String precision) {
    log.debug("Requête REST pour récupérer les zones d'une étude après un import perso begin");
    List<Feature> featureArrayList = new ArrayList<>();

    featureArrayList.addAll(this.zoneService.findZoneByEtudeInterieur(id, Double.parseDouble(precision)).stream()
      .map(this.zoneMapper::toFeature)
      .toList());

    log.debug("findZoneByEtudeInterieur");

    featureArrayList.addAll(this.zoneService.findZoneByEtudeExterieur(id, Double.parseDouble(precision)).stream()
      .map(this.zoneMapper::toFeature)
      .toList());

    log.debug("findZoneByEtudeExterieur");

    Feature[] features = new Feature[featureArrayList.size()];
    FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
    log.debug("Requête REST pour récupérer les zones d'une étude après un import perso end");
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS))
      .cacheControl(CacheControl.noCache())
      .body(featureCollection);
  }

  @GetMapping("/territoire/find")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ResponseEntity<Feature> getTerritoire(@RequestParam("id") Long id,@RequestParam("precision") String precision) {
    log.debug("Requête REST pour récupérer les zones d'une étude");

    Feature feature = this.territoireMapper.toFeature(this.territoireService.findByIdWithPrecision(id,precision));

    return ResponseEntity.ok()
      .body(feature);
  }

  @DeleteMapping("/territoire/delete/{idEtude}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public ResponseEntity<Void> deleteTerritoire(@PathVariable Long idEtude) {
    log.debug("Requête REST pour récupérer les zones d'une étude");

    this.territoireService.deleteTerritoireByIdEtude(idEtude);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/zone/names")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<String>> getZoneNames(@RequestParam("ids") List<Long> ids) {
      log.debug("Requête REST pour récupérer les noms des zones");
      List<String> names = zoneService.getZoneNamesByIds(ids);
      return ResponseEntity.ok(names);
  }
}

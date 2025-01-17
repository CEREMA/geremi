package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;
import fr.cerema.dsi.geremi.config.service.CustomFileValidationService;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.ContrainteEnvironnementaleService;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class ContrainteEnvironnementaleController {

  private static final Logger logger = LoggerFactory.getLogger(ContrainteEnvironnementaleController.class);

  @Autowired
  private ContrainteEnvironnementaleService contrainteEnvironnementaleService;

  @Autowired
  private CustomFileValidationService customFileValidationService;


  @GetMapping("/contrainteEnvironnementale/{idTerritoire}")
  @Operation(summary = "Récupère les contraintes environnementales pour un territoire donné", description = "Permet d'obtenir les contraintes environnementales pour un territoire spécifié dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Contraintes environnementales pour le territoire demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucune contrainte environnementale trouvée pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getContrainteEnvironnementaleByIdTerritoire(
    @PathVariable Long idTerritoire) {
    logger.debug("Récupération des contraintes environnementales pour le territoire avec l'id : {}", idTerritoire);
    Optional<List<Feature>> optionalFeatures = this.contrainteEnvironnementaleService
      .getContrainteEnvironnementaleByIdTerritoire(idTerritoire);

    if (optionalFeatures.isEmpty()) {
      logger.debug("Aucune contrainte environnementale trouvée pour le territoire spécifié");
      return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
    } else {

      List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
      Feature[] features = new Feature[featureArrayList.size()];
      FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
      logger.debug("Retour de {} contraintes environnementales pour le territoire spécifié",
        featureArrayList.size());
      return ResponseEntity.ok().body(featureCollection);
    }
  }

  @GetMapping("/contrainteEnvironnementale/etude/{idEtude}")
  @Operation(summary = "Récupère les contraintes environnementales pour une étude donnée", description = "Permet d'obtenir les contraintes environnementales pour une étude spécifiée dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Contraintes environnementales pour l'étude demandée", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucune contrainte environnementale trouvée pour cette étude")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getContrainteByEtude(@PathVariable Long idEtude) {
    logger.debug("Récupération des contraintes environnementales pour l'étude avec l'id : {}", idEtude);
    Optional<List<Feature>> optionalFeatures = this.contrainteEnvironnementaleService.getContrainteByEtude(idEtude);

    if (optionalFeatures.isEmpty()) {
      logger.debug("Aucune contrainte environnementale trouvée pour l'étude spécifiée");
      return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
    } else {

      List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
      Feature[] features = new Feature[featureArrayList.size()];
      FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
      logger.debug("Retour de {} contraintes environnementales pour l'étude spécifiée", featureArrayList.size());
      return ResponseEntity.ok().body(featureCollection);
    }
  }

  @GetMapping("/contrainteEnvironnementale/existante/{idTerritoire}")
  @Operation(summary = "Récupère les contraintes environnementales existante pour un territoire donné", description = "Permet d'obtenir les contraintes environnementales existante pour un territoire spécifié dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Contraintes environnementales existante pour le territoire demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucune contrainte environnementale existante trouvée pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getExistingContraintesByIdTerritoire(@PathVariable Long idTerritoire) {
    logger.debug("Récupération des contraintes environnementales existante pour le territoire avec l'id : {}",
      idTerritoire);
    Optional<List<Feature>> optionalFeatures = this.contrainteEnvironnementaleService
      .getExistingContraintesByIdTerritoire(idTerritoire);

    if (optionalFeatures.isEmpty()) {
      logger.debug("Aucune contrainte environnementale existante trouvée pour le territoire spécifié");
      return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
    } else {

      List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
      Feature[] features = new Feature[featureArrayList.size()];
      FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
      logger.debug("Retour de {} contraintes environnementales existante pour le territoire spécifié",
        featureArrayList.size());
      return ResponseEntity.ok().body(featureCollection);
    }
  }

  @Operation(summary = "Import des fichers des contraintes environnementales personnalisé", description = "Permet d'enregistrer des contraintes environnementales depuis l'import des fichiers.")
  @ApiResponse(responseCode = "200", description = "L'import demandée")
  @ApiResponse(responseCode = "404", description = "L'import des contraintes environnementales a échoué")
  @PostMapping(value = "/contrainteEnvironnementale/files/{idEtude}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Object> importContrainteEnvironnementaleFiles(
    @RequestParam("files") MultipartFile[] multipartFiles, @PathVariable Long idEtude) {

    try {
      // Vérification
      ValidationImportResult validationImportResult = this.customFileValidationService.validationImport(multipartFiles, idEtude,"contrainte");
      DataStore dataStore = validationImportResult.getDataStore();
      List<String> informativeMessages = validationImportResult.getInformativeMessage();
      // Création
      Optional<List<Integer>> listeContraintes = this.contrainteEnvironnementaleService.createContrainteEnvironnementaleImport(dataStore, idEtude);
      if(listeContraintes.isEmpty()){
        return new ResponseEntity<>("Aucune contrainte n'a été ajouté.", HttpStatus.BAD_REQUEST);
      }
      this.contrainteEnvironnementaleService.isInTerritoire(listeContraintes.get(),idEtude);

      // Si il y a des messages informatifs, renvoyer avec le statut HTTP.
      if (!informativeMessages.isEmpty()) {
        return new ResponseEntity<>(informativeMessages, HttpStatus.CREATED);
      }
    } catch (ImportTypesException | ImportZonageLayerException | ImportZonageGeometryException e) {
      logger.debug("Erreur de contrôle lors de l'import", e);
      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.debug("Erreur interne lors de l'import", e);
      return new ResponseEntity<>("Erreur Interne lors de l'import", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/duplicateContrainte/{idContrEnv}/{idEtude}")
  @Operation(summary = "Duplique une contrainte environnementale existante", description = "Permet de dupliquer une contrainte environnementale existante dans GEREMI")
  @ApiResponse(responseCode = "200", description = "La contrainte environnementale a été dupliquée avec succès")
  @ApiResponse(responseCode = "404", description = "La contrainte environnementale à dupliquer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Integer> duplicateContrainteEnvironnementaleExistante(@PathVariable Integer idContrEnv, @PathVariable Long idEtude) {
    Optional<Integer> id = this.contrainteEnvironnementaleService.duplicateContrainteEnvironnementaleExistante(idContrEnv, idEtude);

    return id.map(integer -> ResponseEntity.ok().body(integer)).orElseGet(() -> ResponseEntity.noContent().build());
  }

  @DeleteMapping("/deleteContrainte/{idContrEnv}")
  @Operation(summary = "Supprime une contrainte environnementale", description = "Permet de supprimer une contrainte environnementale dans GEREMI")
  @ApiResponse(responseCode = "200", description = "La contrainte environnementale a été supprimée avec succès")
  @ApiResponse(responseCode = "404", description = "La contrainte environnementale à supprimer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Integer> deleteContrainteEnvironnementale(@PathVariable Integer idContrEnv) {
    Optional<Integer> id = this.contrainteEnvironnementaleService.deleteContrainteEnvironnementale(idContrEnv);
    return id.map(integer -> ResponseEntity.ok().body(integer)).orElseGet(() -> ResponseEntity.noContent().build());
  }

  @Operation(summary = "Récupère un fichier contrainte.gpkg", description = "Permet de récupérer un fichier contrainte.gpkg, qui contient des informations relatives aux contraintes environnementales.")
  @ApiResponse(responseCode = "200", description = "Le fichier contrainte.gpkg demandé a été récupéré avec succès", content = {
    @Content(mediaType = "application/geopackage+sqlite3", schema = @Schema(type = "string", format = "binary")) })
  @ApiResponse(responseCode = "404", description = "Le fichier contrainte.gpkg n'a pas été trouvé")
  @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier contrainte.gpkg")
  @GetMapping("/contrainteEnvironnementale/gpkg")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Resource> getContrainteEnvironnementaleGpkg() {
    logger.debug("Requête REST pour récupérer le fichier contrainte.gpkg");
    try {
      Resource gpkgResource = new ClassPathResource("data/contrainte.gpkg");

      if (gpkgResource.exists()) {
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrainte.gpkg")
          .contentType(MediaType.parseMediaType("application/geopackage+sqlite3")).body(gpkgResource);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Erreur lors de la récupération du fichier contrainte.gpkg", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

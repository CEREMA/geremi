package fr.cerema.dsi.geremi.controllers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.*;
import fr.cerema.dsi.geremi.services.dto.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import fr.cerema.dsi.commons.datastore.entities.Resultats;
import fr.cerema.dsi.geremi.config.service.CustomFileValidationService;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ImportEtudeController {

  private static final Logger logger = LoggerFactory.getLogger(ImportEtudeController.class);

  @Autowired
  private CustomFileValidationService customFileValidationService;

  @Autowired
  private ImportEtudeService importEtudeService;

  @Operation(summary = "Import du fichier GPKG Etude", description = "Permet d'enregistrer une étude depuis l'import du fichier GPKG.")
  @ApiResponse(responseCode = "200", description = "L'import demandée")
  @ApiResponse(responseCode = "404", description = "L'import Etude a échoué")
  @PostMapping(value = "/importEtude/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ADMIN', 'GEST')")
  public ResponseEntity<Object> importEtudeFile(@RequestParam("files") MultipartFile[] files,
                                                @RequestParam("importEtudeDTO") String jsonImportEtudeDTO) {
    try {

      final ImportEtudeDTO importEtudeDTO = new ObjectMapper().readValue(jsonImportEtudeDTO, ImportEtudeDTO.class);
      // Vérification
      ValidationImportResult validationImportResult = this.customFileValidationService
        .validationImportEtude(files,
          importEtudeDTO.getEtude().getAnneeRef(),
          importEtudeDTO.getEtude().getAnneeFin());
      List<String> informativeMessages = validationImportResult.getInformativeMessage();
      // Si il y a des messages informatifs, renvoyer avec le statut HTTP.
      if (!informativeMessages.isEmpty()) {
        return new ResponseEntity<>(informativeMessages, HttpStatus.CREATED);
      }
      List<Resultats> resultats =  this.customFileValidationService.createResultatsFromImportEtude(files);

      ScenarioDTO scenarioDTO = this.importEtudeService.importEtudeFromFiles(importEtudeDTO,
        validationImportResult.getDataStore(),
        resultats).orElse(null);

      return new ResponseEntity<>(scenarioDTO,HttpStatus.CREATED);
    } catch (IOException | ImportTypesException | ImportZonageLayerException | ImportZonageGeometryException e) {
      logger.debug("Erreur de données lors de l'import : ",e);
      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Erreur lors de l'import de l'étude", e);
      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Récupère un fichier import_etude.gpkg", description = "Permet de récupérer un fichier import_etude.gpkg.")
  @ApiResponse(responseCode = "200", description = "Le fichier import_etude.gpkg demandé a été récupéré avec succès", content = {
    @Content(mediaType = "application/geopackage+sqlite3", schema = @Schema(type = "string", format = "binary")) })
  @ApiResponse(responseCode = "404", description = "Le fichier import_etude.gpkg n'a pas été trouvé")
  @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier import_etude.gpkg")
  @GetMapping("/importEtude/gpkg")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Resource> getImportEtudeGPKG() {
    logger.debug("Requête REST pour récupérer le fichier import_etude.gpkg");
    try {
      Resource gpkgResource = new ClassPathResource("data/import_etude.gpkg");

      if (gpkgResource.exists()) {
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_etude.gpkg")
          .contentType(MediaType.parseMediaType("application/geopackage+sqlite3")).body(gpkgResource);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Erreur lors de la récupération du fichier import_etude.gpkg", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

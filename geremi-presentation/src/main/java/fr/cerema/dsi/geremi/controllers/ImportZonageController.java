package fr.cerema.dsi.geremi.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;
import fr.cerema.dsi.geremi.config.service.CustomFileValidationService;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.dto.TauxCouvertureDTO;
import fr.cerema.dsi.geremi.services.errors.TauxHorsTerritoireException;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class ImportZonageController {
  @Autowired
  private TerritoireService territoireService;
  @Autowired
  private CustomFileValidationService customFileValidationService;

  @GetMapping("/importzonage/check/{idEtude}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Object> importZonageCalculation(@PathVariable Long idEtude){
    TauxCouvertureDTO tauxCouvertureDTO;
    try {
      tauxCouvertureDTO = this.territoireService.calculTauxCouverture(idEtude);
    } catch (TauxHorsTerritoireException e) {
      log.error("Anomalie bloquante lors des calcul sur l'import personnalisé",e);
      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(tauxCouvertureDTO, HttpStatus.OK);
  }

  @Operation(summary = "Import d'un zonage personnalisé", description = "Permet d'enregistrer un zonage depuis l'import d'un fichier.")
  @ApiResponse(responseCode = "200", description = "L'import demandée")
  @ApiResponse(responseCode = "404", description = "L'import du zonage a échoué")
  @PostMapping(value = "/importzonage/files/{idEtude}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json;charset=UTF-8")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Object> importZonageAllFiles(@RequestParam("files") MultipartFile[] multipartFiles, @PathVariable Long idEtude) {
    try {
      // Vérification
    	ValidationImportResult validationImportResult = this.customFileValidationService.validationImport(multipartFiles, idEtude,"zone");
    	DataStore dataStore = validationImportResult.getDataStore();
      this.territoireService.createTerritoireFromImport(dataStore, idEtude);
    } catch (IllegalStateException | IOException e) {
      return new ResponseEntity<>("Erreur Interne lors de l'import", HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (ImportTypesException | ImportZonageLayerException | ImportZonageGeometryException e) {
      log.debug("Erreur lors de l'import d'un gpkg : ",e);
      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok().build();
  }
}

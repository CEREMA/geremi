package fr.cerema.dsi.geremi.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import fr.cerema.dsi.geremi.services.ExportSuiviEtudeService;
import fr.cerema.dsi.geremi.services.ScenarioChantierService;
import fr.cerema.dsi.geremi.services.ScenarioContrainteService;
import fr.cerema.dsi.geremi.services.ScenarioInstallationService;
import fr.cerema.dsi.geremi.services.ScenarioMateriauService;
import fr.cerema.dsi.geremi.services.ScenarioService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Scénaro étude de GEREMI", description = "Permet de gérer les scénario d'une étude de GEREMI")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ScenarioController {

  @Autowired
  private ScenarioService scenarioService;
  @Autowired
  private ScenarioContrainteService scenarioContrainteService;
  @Autowired
  private ScenarioInstallationService scenarioInstallationService;
  @Autowired
  private ScenarioChantierService scenarioChantierService;
  @Autowired
  private ScenarioMateriauService scenarioMateriauService;
  @Autowired
  private ExportSuiviEtudeService exportSuiviEtudeService;

  @Operation(summary = "Récupère tous les scénarios de l'étude", description = "Permet d'obtenir la liste de tous les scénarios de l'étude")
  @ApiResponse(responseCode = "200", description = "Les scénarios de l'étude", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ScenarioDTO.class))) })
  @GetMapping("/scenario/liste/{idEtude}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<ScenarioDTO>> getScenarioEtude(@PathVariable Long idEtude) {
    log.debug("Requête REST pour obtenir toutes les études");
    Optional<List<ScenarioDTO>> scenarioDTOs = this.scenarioService.getListeScenarioEtude(idEtude);
    return scenarioDTOs.map(scenarioDTOS -> new ResponseEntity<>(scenarioDTOS, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.OK));
  }

  @Operation(summary = "Récupère un scénario par son identifiant", description = "Permet d'obtenir un scénario à partir de son identifiant unique")
  @ApiResponse(responseCode = "200", description = "Le scénario  demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Le scénario n'a pas été trouvée")
  @GetMapping("/scenario/{idScenario}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> getScenarioById(@PathVariable Long idScenario) {
    log.debug("Requête REST pour obtenir le scénario avec l'identifiant : {}", idScenario);
    Optional<ScenarioDTO> scenarioDTO = this.scenarioService.getScenarioById(idScenario);
    return scenarioDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "Ajoute un nouveau scénario", description = "Permet d'ajouter une nouveau scénario dans GEREMI")
  @ApiResponse(responseCode = "201", description = "Le scénario a été créée avec succès", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @PostMapping("/scenario/ajouter")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> addScenario(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Requête REST pour ajouter une nouveau scénario : {}", scenarioDTO);
    Optional<ScenarioDTO> responseDTO = this.scenarioService.createScenario(scenarioDTO);
    return responseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "Met à jour une étude existante", description = "Permet de mettre à jour une étude existante dans GEREMI")
  @ApiResponse(responseCode = "200", description = "L'étude a été mise à jour avec succès", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
  @PutMapping("/scenario/modification")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> updateScenario(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Requête REST pour mettre à jour le scenario avec l'identifiant : {} avec les données : {}", scenarioDTO.getId(),scenarioDTO);
    Optional<ScenarioDTO> responseDTO = this.scenarioService.updateScenario(scenarioDTO);
    return responseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "Supprime un scénario existant", description = "Permet de supprimer un scénario existant dans GEREMI")
  @ApiResponse(responseCode = "204", description = "Le scénario a été supprimée avec succès")
  @ApiResponse(responseCode = "404", description = "Le scénario n'a pas été trouvée")
  @DeleteMapping("/scenario/supprimer/{idScenario}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Void> deleteScenario(@PathVariable Long idScenario) {
    log.debug("Requête REST pour supprimer le scenario avec l'identifiant : {}", idScenario);

    if(Objects.nonNull(this.scenarioService.findById(idScenario))){
      this.scenarioService.deleteById(idScenario);
      return ResponseEntity.noContent().build();
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PostMapping("/scenario/ajoutContraintes")
  @Operation(summary = "Ajout des contraintes au scénario", description = "Permet d'ajouter les contraintes sélectionnées au scénario")
  @ApiResponse(responseCode = "200", description = "Installations ajoutées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Aucune cntrainte ajoutée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> addScenarioContraintes(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Ajout des installations au scénario.");
    ScenarioDTO scenarioContrainteDTO = this.scenarioContrainteService.ajoutContrainteScenario(scenarioDTO);

    return ResponseEntity.ok().body(scenarioContrainteDTO);
  }

  @PostMapping("/scenario/ajoutScenarioInstallations")
  @Operation(summary = "Ajout des installations au scénario", description = "Permet d'ajouter les installations sélectionnées au scénario")
  @ApiResponse(responseCode = "200", description = "Installations ajoutées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Aucune installation ajoutée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> addScenarioInstallations(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Ajout des installations au scénario.");
    ScenarioDTO scenarioInstallationDTO = this.scenarioInstallationService.ajoutScenarioInstallation(scenarioDTO);

    return ResponseEntity.ok().body(scenarioInstallationDTO);
  }

  @PostMapping("/scenario/ajoutChantiers")
  @Operation(summary = "Ajout des chantiers au scénario", description = "Permet d'ajouter les chantiers sélectionnées au scénario")
  @ApiResponse(responseCode = "200", description = "Chantiers ajoutées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun chantier ajoutée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> addScenarioChantiers(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Ajout des chantiers au scénario.");
    ScenarioDTO scenarioChantierDTO = this.scenarioChantierService.ajoutScenarioChantier(scenarioDTO);

    return ResponseEntity.ok().body(scenarioChantierDTO);
  }

  @PostMapping("/scenario/ajoutMateriaux")
  @Operation(summary = "Ajout des matériaux au scénario", description = "Permet d'ajouter les matériaux sélectionnées au scénario")
  @ApiResponse(responseCode = "200", description = "Matériaux ajoutées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun matériau ajouté")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> addScenarioMateriau(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Ajout des matériaux au scénario.");
    ScenarioDTO scenarioMateriauDTO = this.scenarioMateriauService.ajoutMateriauScenario(scenarioDTO);

    return ResponseEntity.ok().body(scenarioMateriauDTO);
  }

  @PostMapping("/scenario/retenu")
  @Operation(summary = "Retiens un Scénario d'une étude", description = "Permet de retenir un Scénario")
  @ApiResponse(responseCode = "200", description = "Scénario retenu", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun scénario retenu")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> setScenarioRetenu(@RequestBody ScenarioDTO scenarioDTO) {
    log.debug("Retenir un Scénario.");
    Optional<ScenarioDTO> scenario = this.scenarioService.setScenarioRetenu(scenarioDTO);

    return scenario.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/scenario/suivi/{idEtude}")
  @Operation(summary = "Récupération du scenario validé de l'étude", description = "Récupération du scenario validé de l'étude. Recalcul des productions réelles pour les années disponnibles")
  @ApiResponse(responseCode = "200", description = "Le scénario validé de l'étude", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ScenarioDTO.class))) })
  @ApiResponse(responseCode = "404", description = "Aucun scénario ajouté")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<ScenarioDTO> suiviScenarioEtude(@PathVariable Long idEtude) {
    log.debug("Ajout des matériaux au scénario.");
    ScenarioDTO scenarioDTO = this.scenarioService.suiviScenarioEtude(idEtude);

    scenarioDTO.getResultatsCalculs().get(scenarioDTO.getEtudeDTO().getAnneeRef()).getResultatZones().values()
      .forEach( res -> {
        res.setProductionZonePrimaireBruteReelle(res.getProductionZonePrimaireBrute());
        res.setProductionZonePrimaireTotalReelle(res.getProductionZonePrimaireTotal());
      });

    return ResponseEntity.ok().body(scenarioDTO);
  }

  @GetMapping("/scenario/suivi/export/{idEtude}")
  @Operation(summary = "Export du scenario validé de l'étude", description = "Export du scenario validé de l'étude. Recalcul des productions réelles pour les années disponnibles")

  @ApiResponse(responseCode = "404", description = "Aucun scénario ajouté")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<InputStreamResource> exportScenarioEtude(@PathVariable Long idEtude) {
    log.debug("Export du suivi de l'étude.");
    File csvFile = null;
    FileInputStream fileInputStream = null;
    try {
      csvFile = this.exportSuiviEtudeService.exportSuiviEtudeByIdEtude(idEtude);
      fileInputStream = new FileInputStream(csvFile);

      InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

      return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + csvFile.getName() + "\"")
        .contentType(MediaType.parseMediaType("text/csv;charset=utf-8"))
        .contentLength(csvFile.length())
        .body(inputStreamResource);
    } catch (IOException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } finally {
      if(Objects.nonNull(csvFile)) {
        csvFile.delete();
      }
    }
  }
}

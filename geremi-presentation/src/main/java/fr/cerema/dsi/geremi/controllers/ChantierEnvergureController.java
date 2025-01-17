package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestBody;
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
import fr.cerema.dsi.geremi.services.ChantierEnvergureService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.ChantierEnvergureDTO;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChantierEnvergureController {

	private static final Logger logger = LoggerFactory.getLogger(ChantierEnvergureController.class);



  private ChantierEnvergureService chantierEnvergureService;

  private CustomFileValidationService customFileValidationService;


  public ChantierEnvergureController(ChantierEnvergureService chantierEnvergureService,
                              CustomFileValidationService customFileValidationService,
                              TracabiliteEtapeService tracabiliteEtapeService) {
    this.chantierEnvergureService = chantierEnvergureService;
    this.customFileValidationService = customFileValidationService;
  }

  @GetMapping("/chantierEnvergure/{idTerritoire}")
  @Operation(summary = "Récupère les chantiers pour un territoire donné", description = "Permet d'obtenir les chantiers pour un territoire spécifié dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Chantiers pour le territoire demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun chantier trouvé pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getChantierEnvergureByIdTerritoire(
    @PathVariable Long idTerritoire) {
    logger.debug("Récupération des chantiers pour le territoire avec l'id : {}", idTerritoire);
    Optional<List<Feature>> optionalFeatures = this.chantierEnvergureService
      .getChantierEnvergureByIdTerritoire(idTerritoire);
		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucun chantier trouvé pour le territoire spécifié");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} chantiers pour le territoire spécifié", featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}


  @GetMapping("/chantierEnvergure/etude/{idEtude}")
  @Operation(summary = "Récupère les chantiers pour une étude donnée", description = "Permet d'obtenir les chantiers pour une étude spécifiée dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Chantiers pour l'étude demandée", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun chantier trouvé pour cette étude")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getChantierEnvergureByEtude(@PathVariable Long idEtude) {
    logger.debug("Récupération des chantiers pour l'étude avec l'id : {}", idEtude);
    Optional<List<Feature>> optionalFeatures = this.chantierEnvergureService.getChantierEnvergureByEtude(idEtude);
		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucun chantier trouvé pour l'étude spécifiée");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} chantiers pour l'étude spécifiée", featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}

  @GetMapping("/chantierEnvergure/existant/{idTerritoire}/{idEtude}")
  @Operation(summary = "Récupère les chantiers existants pour un territoire donné", description = "Permet d'obtenir les chantiers existants pour un territoire spécifié dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Chantier existant pour le territoire demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
  @ApiResponse(responseCode = "404", description = "Aucun chantier existante trouvée pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<FeatureCollection> getExistingChantierEnvergureByIdTerritoire(@PathVariable Long idTerritoire,@PathVariable Long idEtude) {
    logger.debug("Récupération des chantiers existants pour le territoire avec l'id : {}",
      idTerritoire);
    Optional<List<Feature>> optionalFeatures = this.chantierEnvergureService
      .getExistingChantiersByIdTerritoire(idTerritoire,idEtude);
		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucun chantier existant trouvé pour le territoire spécifié");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().toList();
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} chantiers existants pour le territoire spécifié", featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}




	@Operation(summary = "Import des fichers des chantiers personnalisé", description = "Permet d'enregistrer des chantiers depuis l'import des fichiers.")
	@ApiResponse(responseCode = "200", description = "L'import demandée")
	@ApiResponse(responseCode = "404", description = "L'import des chantiers a échoué")
	@PostMapping(value = "/chantierEnvergure/files/{idEtude}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Object> importChantierEnvergureFiles(@RequestParam("files") MultipartFile[] multipartFiles,
			@PathVariable Long idEtude) {
		try {
			// Vérification
			ValidationImportResult validationImportResult = this.customFileValidationService
					.validationImport(multipartFiles, idEtude, "chantier");
			DataStore dataStore = validationImportResult.getDataStore();
			// Création
      Optional<List<Integer>> listeChantiers = this.chantierEnvergureService
        .createChantierEnvergureImport(dataStore, idEtude);
      if(listeChantiers.isEmpty()){
        return new ResponseEntity<>("Aucun chantier n'a été ajouté.", HttpStatus.BAD_REQUEST);
      }
      this.chantierEnvergureService.isInTerritoire(listeChantiers.get(), idEtude);
      List<String> informativeMessages = validationImportResult.getInformativeMessage();
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

	@PostMapping("/duplicateChantier/{idChantier}/{idEtude}")
	@Operation(summary = "Duplique un chantier existant", description = "Permet de dupliquer un chantier existant dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Le chantier a été dupliquée avec succès")
	@ApiResponse(responseCode = "404", description = "Le chantier à dupliquer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Integer> duplicateChantierEnvergureExistants(@PathVariable Integer idChantier,
			@PathVariable Long idEtude) {
		Optional<Integer> id = this.chantierEnvergureService.duplicateChantierEnvergureExistante(idChantier, idEtude);
    return id.map(integer -> ResponseEntity.ok().body(integer)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/deleteChantier/{idChantier}")
	@Operation(summary = "Supprime un chantier", description = "Permet de supprimer un chantier dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Le chantier a été supprimée avec succès")
	@ApiResponse(responseCode = "404", description = "Le chantier à supprimer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Integer> deleteChantierEnvergure(@PathVariable Integer idChantier) {
		Optional<Integer> id = this.chantierEnvergureService.deleteChantierEnvergure(idChantier);
    return id.map(integer -> ResponseEntity.ok().body(integer)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

  @GetMapping("/chantierEnvergure/chercher/{idChantier}")
  @Operation(summary = "Cherche un chantier", description = "Permet de chercher un chantier dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Le chantier a été trouvé avec succès")
  @ApiResponse(responseCode = "404", description = "Le chantier n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Feature> getChantierEnvergure(@PathVariable Integer idChantier) {
    Optional<Feature> chantier = this.chantierEnvergureService.findByIdChantier(idChantier);
    return chantier.map(feature -> ResponseEntity.ok().body(feature)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

	@PostMapping("/modificationChantier")
	@Operation(summary = "Duplique un chantier existant", description = "Permet de dupliquer un chantier existant dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Le chantier a été dupliquée avec succès")
	@ApiResponse(responseCode = "404", description = "Le chantier à dupliquer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Integer> modificationChantierEnvergureExistants(
			@RequestBody ChantierEnvergureDTO chantierEnvergureDTO) {
		Optional<Integer> idChantier = this.chantierEnvergureService
				.modificationChantierEnvergure(chantierEnvergureDTO);
    return idChantier.map(integer -> ResponseEntity.ok().body(integer)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Operation(summary = "Récupère un fichier chantier.zip", description = "Permet de récupérer un fichier chantier.zip, qui contient des informations relatives aux chantiers.")
	@ApiResponse(responseCode = "200", description = "Le fichier chantier.zip demandé a été récupéré avec succès", content = {
			@Content(mediaType = "application/geopackage+sqlite3", schema = @Schema(type = "string", format = "binary")) })
	@ApiResponse(responseCode = "404", description = "Le fichier chantier.zip n'a pas été trouvé")
	@ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier chantier.zip")
	@GetMapping("/chantierEnvergure/zip")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Resource> getChantierEnvergureZip() {
		logger.debug("Requête REST pour récupérer le fichier chantier.zip");
		try {
			Resource gpkgResource = new ClassPathResource("data/chantier.zip");

			if (gpkgResource.exists()) {
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chantier.zip")
						.contentType(MediaType.parseMediaType("application/geopackage+sqlite3")).body(gpkgResource);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération du fichier chantier.zip", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

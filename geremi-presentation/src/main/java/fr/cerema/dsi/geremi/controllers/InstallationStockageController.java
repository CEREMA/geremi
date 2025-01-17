package fr.cerema.dsi.geremi.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.exceptions.EntityNotFoundException;
import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;
import fr.cerema.dsi.geremi.config.service.CustomFileValidationService;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.InstallationStockageService;
import fr.cerema.dsi.geremi.services.dto.InstallationStockageDTO;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class InstallationStockageController {

	private static final Logger logger = LoggerFactory.getLogger(InstallationStockageController.class);

	@Autowired
	private InstallationStockageService installationStockageService;

	@Autowired
	private CustomFileValidationService customFileValidationService;

	@GetMapping("/installationStockage/{idTerritoire}")
	@Operation(summary = "Récupère les installations de stockage pour un territoire donné", description = "Permet d'obtenir les installations de stockage pour un territoire spécifié dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Installations de stockage pour le territoire demandé", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
	@ApiResponse(responseCode = "404", description = "Aucune installation de stockage trouvée pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<FeatureCollection> getInstallationStockageByIdTerritoire(@PathVariable Long idTerritoire) {
		logger.debug("Récupération des installations de stockage pour le territoire avec l'id : {}", idTerritoire);
		Optional<List<Feature>> optionalFeatures = this.installationStockageService
				.getInstallationStockageByIdTerritoire(idTerritoire);

		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucune installation de stockage trouvée pour le territoire spécifié");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().collect(Collectors.toList());
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} installations de stockage pour le territoire spécifié", featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}

	@GetMapping("/installationStockage/etude/{idEtude}")
	@Operation(summary = "Récupère les installations de stockage pour une étude donnée", description = "Permet d'obtenir les installations de stockage pour une étude spécifiée dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Installations de stockage pour l'étude demandée", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
	@ApiResponse(responseCode = "404", description = "Aucune installation de stockage trouvée pour cette étude")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<FeatureCollection> getInstallationStockageByEtude(@PathVariable Long idEtude) {
		logger.debug("Récupération des installations de stockage pour l'étude avec l'id : {}", idEtude);
		Optional<List<Feature>> optionalFeatures = this.installationStockageService
				.getInstallationStockageByEtude(idEtude);

		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucune installation de stockage trouvée pour l'étude spécifiée");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().collect(Collectors.toList());
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} installations de stockage pour l'étude spécifiée", featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}

	@GetMapping("/installationStockage/existante/{idTerritoire}/{idEtude}")
	@Operation(summary = "Récupère les installations de stockage existantes pour un territoire donné", description = "Permet d'obtenir les installations de stockage existantes pour un territoire spécifié dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Installations de stockage existantes pour le territoire demandé", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureCollection.class)) })
	@ApiResponse(responseCode = "404", description = "Aucune installation de stockage existante trouvée pour ce territoire")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<FeatureCollection> getExistingInstallationsByIdTerritoire(@PathVariable Long idTerritoire,
			@PathVariable Long idEtude) {
		logger.debug("Récupération des installations de stockage existantes pour le territoire avec l'id : {}",
				idTerritoire);
		Optional<List<Feature>> optionalFeatures = this.installationStockageService
				.getExistingInstallationStockageByIdTerritoire(idTerritoire, idEtude);

		if (optionalFeatures.isEmpty()) {
			logger.debug("Aucune installation de stockage existante trouvée pour le territoire spécifié");
			return ResponseEntity.ok().body(new FeatureCollection(new Feature[] {}));
		} else {

			List<Feature> featureArrayList = optionalFeatures.get().stream().collect(Collectors.toList());
			Feature[] features = new Feature[featureArrayList.size()];
			FeatureCollection featureCollection = new FeatureCollection(featureArrayList.toArray(features));
			logger.debug("Retour de {} installations de stockage existantes pour le territoire spécifié",
					featureArrayList.size());
			return ResponseEntity.ok().body(featureCollection);
		}
	}

	@Operation(summary = "Import des fichiers des installations de stockage personnalisées", description = "Permet d'enregistrer des installations de stockage depuis l'import des fichiers.")
	@ApiResponse(responseCode = "200", description = "L'import demandée")
	@ApiResponse(responseCode = "404", description = "L'import des installations de stockage a échoué")
	@PostMapping(value = "/installationStockage/files/{idEtude}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Object> importInstallationStockageFiles(@RequestParam("files") MultipartFile[] multipartFiles,
			@PathVariable Long idEtude) {
		try {
			// Vérification
			ValidationImportResult validationImportResult = this.customFileValidationService.validationImport(multipartFiles, idEtude, "stockage");
			DataStore dataStore = validationImportResult.getDataStore();
			List<String> informativeMessages = validationImportResult.getInformativeMessage();

			// Création
			this.installationStockageService.createInstallationStockageImport(dataStore, idEtude);

			// Si il y a des messages informatifs, renvoyer avec le statut HTTP.
			if (!informativeMessages.isEmpty()) {
				return new ResponseEntity<>(informativeMessages, HttpStatus.CREATED);
			}

		} catch (IOException e) {
			logger.debug("Erreur interne lors de l'import", e);
			return new ResponseEntity<>("Erreur Interne lors de l'import", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ImportTypesException | ImportZonageLayerException | ImportZonageGeometryException e) {
			logger.debug("Erreur de contrôle lors de l'import", e);
			return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}


	@PostMapping("/duplicateInstallationStockage/{idInstallationStockage}/{idEtude}")
	@Operation(summary = "Duplique une installation de stockage existante", description = "Permet de dupliquer une installation de stockage existante dans GEREMI")
	@ApiResponse(responseCode = "200", description = "L'installation de stockage a été dupliquée avec succès")
	@ApiResponse(responseCode = "404", description = "L'installation de stockage à dupliquer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Integer> duplicateInstallationStockageExistante(@PathVariable Integer idInstallationStockage,
			@PathVariable Long idEtude) {
		Optional<Integer> id = this.installationStockageService
				.duplicateInstallationStockageExistante(idInstallationStockage, idEtude);
		return ResponseEntity.ok().body(id.get());
	}

	@DeleteMapping("/deleteInstallationStockage/{idInstallationStockage}")
	@Operation(summary = "Supprime une installation de stockage", description = "Permet de supprimer une installation de stockage dans GEREMI")
	@ApiResponse(responseCode = "200", description = "L'installation de stockage a été supprimée avec succès")
	@ApiResponse(responseCode = "404", description = "L'installation de stockage à supprimer n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Integer> deleteInstallationStockage(@PathVariable Integer idInstallationStockage) {
		Optional<Integer> id = this.installationStockageService.deleteInstallationStockage(idInstallationStockage);
		return ResponseEntity.ok().body(id.get());
	}

	@Operation(summary = "Récupère un fichier stockage.gpkg", description = "Permet de récupérer un fichier stockage.gpkg, qui contient des informations relatives aux installations de stockage.")
	@ApiResponse(responseCode = "200", description = "Le fichier stockage.gpkg demandé a été récupéré avec succès", content = {
			@Content(mediaType = "application/geopackage+sqlite3", schema = @Schema(type = "string", format = "binary")) })
	@ApiResponse(responseCode = "404", description = "Le fichier stockage.gpkg n'a pas été trouvé")
	@ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier stockage.gpkg")
	@GetMapping("/installationStockage/gpkg")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Resource> getInstallationStockageGpkg() {
		logger.debug("Requête REST pour récupérer le fichier stockage.gpkg");
		try {
			Resource gpkgResource = new ClassPathResource("data/stockage.gpkg");

			if (gpkgResource.exists()) {
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stockage.gpkg")
						.contentType(MediaType.parseMediaType("application/geopackage+sqlite3")).body(gpkgResource);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération du fichier stockage.gpkg", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/updateDataInstallationStockage/{id}")
	@Operation(summary = "Mettre à jour une installation de stockage", description = "Permet de mettre à jour une installation de stockage dans GEREMI")
	@ApiResponse(responseCode = "201", description = "L'installation de stockage a été mise à jour avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InstallationStockageDTO.class)) })
	@ApiResponse(responseCode = "400", description = "Les données fournies sont invalides")
	@ApiResponse(responseCode = "404", description = "L'installation de stockage à mettre à jour n'a pas été trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Object> updateInstallationStockageById(@PathVariable Integer id,
			@RequestBody InstallationStockageDTO installationStockageDTO) {
		try {
			installationStockageService.updateInstallationStockageById(id, installationStockageDTO);
			return ResponseEntity.ok(installationStockageDTO);
		} catch (EntityNotFoundException e) {
			logger.error("Installation de stockage non trouvée avec l'ID: {}", id, e);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			logger.error("Erreur lors de la mise à jour de l'installation de stockage avec l'ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erreur lors de la mise à jour de l'installation de stockage");
		}
	}

	@GetMapping("/installationStockage/existante/{id}")
	@Operation(summary = "Récupère une installation de stockage par son identifiant", description = "Permet de récupérer une installation de stockage spécifique par son identifiant dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Installation de stockage trouvée", content = {
	    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Feature.class)) })
	@ApiResponse(responseCode = "404", description = "Installation de stockage non trouvée")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Feature> getInstallationStockageById(@PathVariable Integer id) {
	    Optional<Feature> installationStockageFeatureOptional = installationStockageService.findFeatureById(id);
    return installationStockageFeatureOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}

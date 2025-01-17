package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.cerema.dsi.geremi.services.dto.RegionDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.commons.exceptions.WorkflowSecurityException;
import fr.cerema.dsi.geremi.config.service.CustomFileService;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.EtudeDTO;
import fr.cerema.dsi.geremi.services.dto.EtudesUtilisateurDTO;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Etudes de GEREMI", description = "Permet de gérer les études de GEREMI")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class EtudeController {

	private final Logger log = LoggerFactory.getLogger(EtudeController.class);

	private final EtudeService etudeService;

	private final CustomFileService customFileService;

  private final TracabiliteEtapeService tracabiliteEtapeService;

	public EtudeController(EtudeService etudeService, CustomFileService customFileService, TracabiliteEtapeService tracabiliteEtapeService) {
		this.etudeService = etudeService;
		this.customFileService = customFileService;
		this.tracabiliteEtapeService = tracabiliteEtapeService;
	}

	@Operation(summary = "Récupère toutes de l'utilisateur", description = "Permet d'obtenir la liste de toutes les études visible par l'utilisateur courant")
	@ApiResponse(responseCode = "200", description = "Les études demandées", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = EtudesUtilisateurDTO.class))) })
	@GetMapping("/etudes")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<EtudesUtilisateurDTO> getEtudesCurrentUser() {
		log.debug("Requête REST pour obtenir toutes les études");
		EtudesUtilisateurDTO etudesUtilisateurDTO = this.etudeService.findEtudeUtilisateur();
		if (etudesUtilisateurDTO != null) {
			return new ResponseEntity<>(etudesUtilisateurDTO, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Récupère une étude par son identifiant", description = "Permet d'obtenir une étude à partir de son identifiant unique")
	@ApiResponse(responseCode = "200", description = "L'étude demandée", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EtudeDTO.class)) })
	@ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
	@GetMapping("/etudes/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<EtudeDTO> getEtudeById(@PathVariable Long id) {
		log.debug("Requête REST pour obtenir l'étude avec l'identifiant : {}", id);
		Optional<EtudeDTO> etudeDTO = this.etudeService.getEtudeById(id);
    return etudeDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

	@Operation(summary = "Ajoute une nouvelle étude", description = "Permet d'ajouter une nouvelle étude dans GEREMI")
	@ApiResponse(responseCode = "201", description = "L'étude a été créée avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EtudeDTO.class)) })
	@PostMapping("/etudes")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<EtudeDTO> addEtude(@RequestBody EtudeDTO etudeDTO) {
		log.debug("Requête REST pour ajouter une nouvelle étude : {}", etudeDTO);
		EtudeDTO responseDTO = this.etudeService.save(etudeDTO);
		return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
	}

	@Operation(summary = "Met à jour une étude existante", description = "Permet de mettre à jour une étude existante dans GEREMI")
	@ApiResponse(responseCode = "200", description = "L'étude a été mise à jour avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EtudeDTO.class)) })
	@ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
	@PutMapping("/etudes/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<EtudeDTO> updateEtude(@PathVariable Long id, @RequestBody EtudeDTO etudeDTO) {
		log.debug("Requête REST pour mettre à jour l'étude avec l'identifiant : {} avec les données : {}", id,
				etudeDTO);
		Optional<EtudeDTO> responseDTO = this.etudeService.updateEtude(id, etudeDTO);
    return responseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "Publie une etude", description = "Permet de publier une étude")
  @ApiResponse(responseCode = "200", description = "L'étude a été mise à jour avec succès", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EtudeDTO.class)) })
  @ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
  @PutMapping("/etudes/publier")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<EtudeDTO> publierEtude(@RequestBody EtudeDTO etudeDTO) {
    log.debug("Requête REST pour mettre à jour l'étude avec l'identifiant : {}",
      etudeDTO);
    Optional<EtudeDTO> responseDTO = this.etudeService.publierEtude(etudeDTO);
    return responseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

	@Operation(summary = "Supprime une étude existante", description = "Permet de supprimer une étude existante dans GEREMI")
	@ApiResponse(responseCode = "204", description = "L'étude a été supprimée avec succès")
	@ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
	@DeleteMapping("/etudes/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Void> deleteEtude(@PathVariable Long id) {
		log.debug("Requête REST pour supprimer l'étude avec l'identifiant : {}", id);
		Etude existingEtude = this.etudeService.findById(id);
		if (existingEtude != null) {
			this.etudeService.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Récupère un fichier ODS pour une étude spécifique", description = "Permet de récupérer un fichier ODS rempli avec les données de l'étude correspondant à l'ID de l'étude")
	@ApiResponse(responseCode = "200", description = "Le fichier ODS demandé a été récupéré avec succès", content = {
			@Content(mediaType = "application/vnd.oasis.opendocument.text", schema = @Schema(type = "string", format = "binary")) })
	@ApiResponse(responseCode = "404", description = "L'étude ou le fichier ODS n'a pas été trouvé")
	@ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier ODS")
	@GetMapping("/etudes/{id}/ods")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Resource> getEtudeOdt(@PathVariable Long id) {
		log.debug("Requête REST pour récupère un fichier ODS pour une étude spécifique avec l'identifiant : {}", id);
		try {
			Resource odtResource = this.customFileService.getOdsForEtude(id);

			if (odtResource != null) {
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=modele_population.ods")
						.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.text"))
						.body(odtResource);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du fichier ODS de l'étude {}", id, e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Upload un fichier (ODS, XLS ou XLSX) pour une étude spécifique", description = "Permet d'upload un fichier (ODS, XLS ou XLSX) pour une étude correspondant à l'ID de l'étude")
	@ApiResponse(responseCode = "200", description = "Le fichier a été uploadé avec succès")
	@ApiResponse(responseCode = "400", description = "Le fichier n'est pas au format ODS, XLS, XLSX ou est vide")
	@ApiResponse(responseCode = "404", description = "L'étude n'a pas été trouvée")
	@PostMapping(value = "/etudes/{id}/uploadfile", produces = "application/json;charset=UTF-8")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Object> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		log.debug(
				"Requête REST pour uploader un fichier (ODS, XLS ou XLSX) pour une étude spécifique avec l'identifiant : {}",
				id);

		Map<String, Zone> zones;
		try {
			// Créer la map ici
			zones = this.customFileService.findZonesByCode(id);
		} catch (Exception e) {
			log.error("Erreur lors de la récupération des zones pour l'étude {}", id, e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			List<PopulationDTO> populationDataList = this.customFileService.checkFileColumns(id, file, zones);
			return new ResponseEntity<>(populationDataList, HttpStatus.OK);
		} catch (Exception e) {
			log.debug("Le fichier n'est pas au format ODS, XLS, XLSX ou est vide", e);
			return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Récupère un fichier zones.gpkg", description = "Permet de récupérer un fichier zones.gpkg contenant les informations géographiques des zones")
	@ApiResponse(responseCode = "200", description = "Le fichier zones.gpkg demandé a été récupéré avec succès", content = {
			@Content(mediaType = "application/geopackage+sqlite3", schema = @Schema(type = "string", format = "binary")) })
	@ApiResponse(responseCode = "404", description = "Le fichier zones.gpkg n'a pas été trouvé")
	@ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération du fichier zones.gpkg")
	@GetMapping("/etudes/gpkg")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Resource> getZonesGpkg() {
		log.debug("Requête REST pour récupérer le fichier zones.gpkg");
		try {
			Resource gpkgResource = new ClassPathResource("data/zones.gpkg");

			if (gpkgResource.exists()) {
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=zones.gpkg")
						.contentType(MediaType.parseMediaType("application/geopackage+sqlite3")).body(gpkgResource);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du fichier zones.gpkg", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


  @PutMapping("/etudes/{etude}/tracabilite/{etape}/{etat}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<Map<Etape, EtatEtape>> addTracabiliteEtape(@PathVariable Long etude, @PathVariable String etape, @PathVariable String etat) {
    Etape etapeEnum = Etape.valueOf(etape);

    EtatEtape etatEtapeEnum =  EtatEtape.valueOf(etat);

    try {
      Map<Etape, EtatEtape> etatEtapes = tracabiliteEtapeService.addTracabiliteEtape(etude, null, etapeEnum, etatEtapeEnum);
      return new ResponseEntity<>(etatEtapes, HttpStatus.OK);
    } catch (WorkflowSecurityException e) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

  }

  @Operation(summary = "Récupère toutes de l'utilisateur", description = "Permet d'obtenir la liste de toutes les études visible par l'utilisateur courant")
  @ApiResponse(responseCode = "200", description = "Les études demandées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = EtudesUtilisateurDTO.class))) })
  @GetMapping("/etudes/suivi")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<EtudesUtilisateurDTO> getEtudesValideesCurrentUser() {
    log.debug("Requête REST pour obtenir toutes les études");
    EtudesUtilisateurDTO etudesUtilisateurDTO = this.etudeService.findEtudeSuiviUtilisateur();
    if (etudesUtilisateurDTO != null) {
      return new ResponseEntity<>(etudesUtilisateurDTO, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Récupère toutes les études public", description = "Permet d'obtenir la liste de toutes les études public")
  @ApiResponse(responseCode = "200", description = "Les études demandées", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
  @GetMapping("/etudes/public")
  public ResponseEntity<Map<RegionDTO,List<EtudeDTO>>> getEtudesPublic() {
    log.debug("Requête REST pour obtenir toutes les études");
    Map<RegionDTO,List<EtudeDTO>> mapEtudePublic = this.etudeService.findEtudePublicInRegion();
    if (mapEtudePublic != null) {
      return new ResponseEntity<>(mapEtudePublic, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}

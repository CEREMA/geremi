package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;
import fr.cerema.dsi.geremi.services.RelEtudeUserProcurationService;
import fr.cerema.dsi.geremi.services.dto.RelEtudeUserProcurationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Relations entre les études, les utilisateurs et les procurations", description = "Permet de gérer les relations entre les études, les utilisateurs et les procurations")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class RelEtudeUserProcurationController {

	private final Logger log = LoggerFactory.getLogger(RelEtudeUserProcurationController.class);

	private final RelEtudeUserProcurationService relEtudeUserProcurationService;

	public RelEtudeUserProcurationController(RelEtudeUserProcurationService relEtudeUserProcurationService) {
		this.relEtudeUserProcurationService = relEtudeUserProcurationService;
	}

	@Operation(summary = "Récupère une relation étude-utilisateur-procuration par son identifiant", description = "Permet d'obtenir une relation étude-utilisateur-procuration à partir de son identifiant unique")
	@ApiResponse(responseCode = "200", description = "La relation demandée", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RelEtudeUserProcuration.class)) })
	@ApiResponse(responseCode = "404", description = "La relation n'a pas été trouvée")
	@GetMapping("/procuration/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<RelEtudeUserProcurationDTO> getRelEtudeUserProcurationById(@PathVariable Long id) {
		log.debug("Requête REST pour obtenir la relation étude-utilisateur-procuration avec l'identifiant : {}", id);
    Optional<RelEtudeUserProcurationDTO> relEtudeUserProcurationDTO = this.relEtudeUserProcurationService.getRelEtudeUserProcurationById(id);
    return relEtudeUserProcurationDTO.map(etudeUserProcurationDTO -> new ResponseEntity<>(etudeUserProcurationDTO, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

	@Operation(summary = "Ajoute une nouvelle relation entre une étude, un utilisateur et une procuration", description = "Permet d'ajouter une nouvelle relation entre une étude, un utilisateur et une procuration dans GEREMI")
	@ApiResponse(responseCode = "201", description = "La relation a été créée avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RelEtudeUserProcuration.class)) })
	@PostMapping("/procuration")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<RelEtudeUserProcurationDTO> addRelEtudeUserProcuration(
			@RequestBody RelEtudeUserProcurationDTO relEtudeUserProcurationDTO) {
		log.debug(
				"Requête REST pour ajouter une nouvelle relation entre une étude, un utilisateur et une procuration : {}",
				relEtudeUserProcurationDTO);
    RelEtudeUserProcurationDTO responseDTO = this.relEtudeUserProcurationService.addRelEtudeUserProcuration(relEtudeUserProcurationDTO);
		return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
	}


  @Operation(summary = "Met à jour une relation existante", description = "Permet de mettre à jour une relation existante dans GEREMI")
  @ApiResponse(responseCode = "200", description = "La relation a été mise à jour avec succès", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RelEtudeUserProcuration.class)) })
  @ApiResponse(responseCode = "404", description = "La relation n'a pas été trouvée")
  @PutMapping("/procuration/update/{idEtude}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<RelEtudeUserProcurationDTO>> updateListeRelEtudeUserProcuration(@PathVariable Long idEtude,
                                                                                  @RequestBody List<RelEtudeUserProcurationDTO> relEtudeUserProcurationDTO) {
    log.debug("Requête REST pour mettre à jour les relations Etudes User : {} avec les données : {}", idEtude, relEtudeUserProcurationDTO);

    List<RelEtudeUserProcurationDTO> responseDTO = this.relEtudeUserProcurationService.updateRelByIdEtude(idEtude,relEtudeUserProcurationDTO);
    return new ResponseEntity<>(responseDTO, HttpStatus.OK);
  }
}

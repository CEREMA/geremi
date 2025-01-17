package fr.cerema.dsi.geremi.controllers;

import fr.cerema.dsi.geremi.services.CalculsProductionAndProjectionService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Calculs de Production", description = "Permet de gérer les calculs de production")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class CalculsProductionController {


	private final CalculsProductionAndProjectionService calculsProductionAndProjectionService;



	public CalculsProductionController(CalculsProductionAndProjectionService calculsProductionAndProjectionService) {
		this.calculsProductionAndProjectionService = calculsProductionAndProjectionService;
  }

  @Operation(summary = "Exécute le calcul de production pour une étude spécifique et une année de référence", description = "Exécute le calcul de production pour une étude correspondant à l'ID de l'étude et une année de référence")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Le calcul de production a été exécuté avec succès"),
    @ApiResponse(responseCode = "404", description = "L'étude ou le scénario n'a pas été trouvée"),
    @ApiResponse(responseCode = "500", description = "Erreur interne lors de l'exécution du calcul de production") })
  @PostMapping("/calculsproductiondepartement/{idEtude}/{idScenario}/{anneeRef}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<ScenarioDTO> executeCalculProductionPost(@PathVariable Long idEtude, @PathVariable Long idScenario, @PathVariable int anneeRef,
                                                                             @RequestBody ScenarioDTO scenarioDTO) {
    log.debug(
      "Requête REST pour exécuter le calcul de production pour l'étude avec l'identifiant : {} et l'année de référence : {}",
      idEtude, anneeRef);
    try {
      ScenarioDTO updatedScenarioDTO = this.calculsProductionAndProjectionService.executeCalculProductionAnneeRef(idEtude, idScenario, anneeRef, scenarioDTO);

      return ResponseEntity.ok(updatedScenarioDTO);
    } catch (Exception e) {
      log.error(
        "Erreur lors de l'exécution du calcul de production pour l'étude avec l'identifiant : {} et l'année de référence : {}",
        idEtude, anneeRef, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

	@Operation(summary = "Exécute le calcul de projection pour un scénario spécifique", description = "Exécute le calcul de projection pour un scénario correspondant à l'ID du scénario")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Le calcul de projection a été exécuté avec succès", content = {
	        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ScenarioDTO.class)) }),
	    @ApiResponse(responseCode = "400", description = "Le scénario envoyé est invalide"),
	    @ApiResponse(responseCode = "404", description = "Le scénario n'a pas été trouvé"),
	    @ApiResponse(responseCode = "500", description = "Erreur interne lors de l'exécution du calcul de projection") })
	@PostMapping("/calculsprojectionzone")
	@PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<ScenarioDTO> executeCalculProjectionBesoins(@RequestBody ScenarioDTO scenarioDTO) {
	    log.debug("Requête REST pour exécuter le calcul de projection pour le scénario avec l'identifiant : {}", scenarioDTO.getId());

	    if (scenarioDTO == null || scenarioDTO.getId() == null) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	    try {
	        ScenarioDTO scenarioUpdatedDTO = this.calculsProductionAndProjectionService.projectionScenario(scenarioDTO);
	        if (scenarioUpdatedDTO == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }

	        return ResponseEntity.ok().body(scenarioUpdatedDTO);
	    } catch (Exception e) {
	        log.error("Erreur lors de l'exécution du calcul de projection pour le scénario avec l'identifiant : {}", scenarioDTO.getId(), e);
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


}

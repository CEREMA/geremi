package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import fr.cerema.dsi.geremi.entities.Population;
import fr.cerema.dsi.geremi.services.PopulationService;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;
import fr.cerema.dsi.geremi.services.mapper.PopulationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Populations de GEREMI", description = "Permet de gérer les populations de GEREMI")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class PopulationController {

	private final Logger log = LoggerFactory.getLogger(PopulationController.class);

	@Autowired
	private final PopulationService populationService;

	@Autowired
	private final PopulationMapper populationMapper;

	@Autowired
	public PopulationController(PopulationService populationService, PopulationMapper populationMapper) {
		this.populationService = populationService;
		this.populationMapper = populationMapper;
	}

	@Operation(summary = "Récupère toutes les populations", description = "Permet d'obtenir la liste de toutes les populations enregistrées dans GEREMI")
	@ApiResponse(responseCode = "200", description = "Les populations demandées", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Population.class))) })
	@GetMapping("/populations")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<List<PopulationDTO>> getAllPopulations() {
		log.debug("Requête REST pour obtenir toutes les populations");
		List<PopulationDTO> populationDTOList = this.populationService.findAll().stream()
				.map(this.populationMapper::toDto).collect(Collectors.toList());
		return new ResponseEntity<>(populationDTOList, HttpStatus.OK);
	}

	@Operation(summary = "Ajoute une liste de populations", description = "Permet d'ajouter une liste de populations dans GEREMI")
	@ApiResponse(responseCode = "201", description = "Les populations ont été créées avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Population.class)) })
	@PutMapping(value = "/populations/{idEtude}/add", produces = "application/json;charset=UTF-8")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<List<PopulationDTO>> addPopulation(@PathVariable Long idEtude, @RequestBody List<PopulationDTO> populationDTO) {
		log.debug("Requête REST pour ajouter une liste de populations : {}", populationDTO);
    List<PopulationDTO> responseDTO = this.populationService.addPopulations(idEtude, populationDTO);
		return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
	}

	@Operation(summary = "Récupère une population par son identifiant", description = "Permet d'obtenir une population à partir de son identifiant unique")
	@ApiResponse(responseCode = "200", description = "La population demandée", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Population.class)) })
	@ApiResponse(responseCode = "404", description = "La population n'a pas été trouvée")
	@GetMapping("/populations/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<PopulationDTO> getPopulationById(@PathVariable Integer id) {
		log.debug("Requête REST pour obtenir la population avec l'identifiant : {}", id);
		Population population = this.populationService.findById(id);
		if (population != null) {
			PopulationDTO populationDTO = this.populationMapper.toDto(population);
			return new ResponseEntity<>(populationDTO, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Met à jour une population existante", description = "Permet de mettre à jour une population existante dans GEREMI")
	@ApiResponse(responseCode = "200", description = "La population a été mise à jour avec succès", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Population.class)) })
	@ApiResponse(responseCode = "404", description = "La population n'a pas été trouvée")
	@PostMapping("/populations/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<PopulationDTO> updatePopulation(@PathVariable Integer id,
			@RequestBody PopulationDTO populationDTO) {
		log.debug("Requête REST pour mettre à jour la population avec l'identifiant : {} avec les données : {}", id,
				populationDTO);
		Population existingPopulation = this.populationService.findById(id);
		if (existingPopulation != null) {
			Population population = this.populationMapper.toEntity(populationDTO);
			population.setIdPopulation(id);
			population = this.populationService.save(population);
			PopulationDTO responseDTO = this.populationMapper.toDto(population);
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Supprime une population existante", description = "Permet de supprimer une population existante dans GEREMI")
	@ApiResponse(responseCode = "204", description = "La population a été supprimée avec succès")
	@ApiResponse(responseCode = "404", description = "La population n'a pas été trouvée")
	@DeleteMapping("/populations/{id}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
	public ResponseEntity<Void> deletePopulation(@PathVariable Integer id) {
		log.debug("Requête REST pour supprimer la population avec l'identifiant : {}", id);
		Population existingPopulation = this.populationService.findById(id);
		if (existingPopulation != null) {
			this.populationService.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}

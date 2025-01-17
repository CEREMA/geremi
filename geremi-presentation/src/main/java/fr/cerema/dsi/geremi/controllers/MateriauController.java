package fr.cerema.dsi.geremi.controllers;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cerema.dsi.geremi.services.MateriauService;
import fr.cerema.dsi.geremi.services.dto.MateriauDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class MateriauController {

  @Autowired
  private MateriauService materiauService;

  @GetMapping("/materiau/liste")
  @Operation(summary = "Récupère la liste des matériaux disponibles", description = "Permet d'obtenir les matériaux disponibles dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Matériaux disponibles", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
  @ApiResponse(responseCode = "404", description = "Aucun matériau disponible")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<MateriauDTO>> getMateriauxDisponibles() {
    log.debug("Récupération des matériaux disponibles.");
    List<MateriauDTO> materiauxDisponibles = this.materiauService
      .getMateriauxDisponibles()
      .orElse(new ArrayList<>());

    return ResponseEntity.ok().body(materiauxDisponibles);
  }

  @GetMapping("/materiau/{idEtude}")
  @Operation(summary = "Récupère la liste des matériaux disponibles", description = "Permet d'obtenir les matériaux disponibles dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Matériaux disponibles", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
  @ApiResponse(responseCode = "404", description = "Aucun matériau disponible")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<MateriauDTO>> getMateriauxEtude(@PathVariable Long idEtude) {
    log.debug("Récupération des matériaux disponibles.");
    List<MateriauDTO> materiauxEtude = this.materiauService
      .getMateriauxEtude(idEtude)
      .orElse(new ArrayList<>());

    return ResponseEntity.ok().body(materiauxEtude);
  }

  @PostMapping("/materiau/add")
  @Operation(summary = "Ajout d'un matériau", description = "Permet d'ajouter un matériau dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Ajout d'un matériau", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
  @ApiResponse(responseCode = "404", description = "Aucun matériau ajouté")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<MateriauDTO> addMateriauxEtude(@RequestBody MateriauDTO materiauDTO) {
    log.debug("Ajout d'un matériau.");
    MateriauDTO materiau = this.materiauService.ajoutMateriauEtude(materiauDTO);

    return ResponseEntity.ok().body(materiau);
  }

  @DeleteMapping("/materiau/supprimer/{idMateriau}")
  @Operation(summary = "Supprimer un matériau", description = "Permet de supprimer un matériau dans GEREMI")
  @ApiResponse(responseCode = "200", description = "Supprimer un matériau", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
  @ApiResponse(responseCode = "404", description = "Aucun matériau supprimé")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<MateriauDTO> supprimerMateriauxEtude(@PathVariable Integer idMateriau) {
    log.debug("Suppression du materiau.");
    this.materiauService.supprimerMateriaux(idMateriau);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}

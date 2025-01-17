package fr.cerema.dsi.geremi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.dto.TauxCouvertureDTO;
import fr.cerema.dsi.geremi.services.errors.TauxHorsTerritoireException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class VerificationTerritoireController {

  @Autowired
  private TerritoireService territoireService;

  @GetMapping("/verification/tauxhorsFrance")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<TauxCouvertureDTO> findTauxTerritoireHorsFrance(@RequestParam("idEtude") Long idEtude) throws TauxHorsTerritoireException {
    log.debug("Requête REST pour récupérer le taux Hors France d'un territoire");

    TauxCouvertureDTO tauxhorsFrance = this.territoireService.findTauxTerritoireHorsFrance(idEtude);

    return ResponseEntity.ok()
      .body(tauxhorsFrance);
  }

  @GetMapping("/verification/tauxhorsRegion")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<TauxCouvertureDTO> findTauxTerritoireHorsRegion(@RequestParam("idEtude") Long idEtude, @RequestParam("idRegion") Long idRegion) throws TauxHorsTerritoireException {
    log.debug("Requête REST pour récupérer le taux Hors Région de l'utilisateur d'un territoire");

    TauxCouvertureDTO tauxhorsRegion = this.territoireService.findTauxTerritoireHorsRegion(idEtude,idRegion);

    return ResponseEntity.ok()
      .body(tauxhorsRegion);
  }
}
